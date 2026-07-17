package com.callover.android.core.call_coordinator

import android.util.Log
import com.callover.android.core.calls.CallEndReason
import com.callover.android.core.calls.CallState
import com.callover.android.core.calls.CallStore
import com.callover.android.core.data.calls.CallsRepository
import com.callover.android.core.domain.models.CallStatus
import com.callover.android.core.domain.models.RemoteDescriptionType
import com.callover.android.core.network.ApiResult
import com.callover.android.core.realtime.signaling.SignalingEvent
import com.callover.android.core.realtime.signaling.SignalingRealtimeDataSource
import com.callover.android.core.signaling.SignalingService
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallCoordinator @Inject constructor(
    private val callStore: CallStore,
    private val callsRepository: CallsRepository,
    private val signalingRealtimeDataSource: SignalingRealtimeDataSource,
    private val signalingService: SignalingService,
) {
    val callState: StateFlow<CallState> = callStore.state

    suspend fun collectSignalingEvents() {
        signalingRealtimeDataSource.events.collect { event ->
            handleSignalingEvent(event)
        }
    }

    suspend fun restoreCurrentRingingCallIfNeeded() {
        if (callStore.isBusy) {
            return
        }

        when (val result = callsRepository.getCurrentRingingCall()) {
            is ApiResult.Success -> {
                val call = result.data.call ?: return

                if (call.status != CallStatus.Ringing) {
                    return
                }

                val remoteDescription = call.remoteDescription ?: return

                if (remoteDescription.type != RemoteDescriptionType.Offer) {
                    return
                }

                val registered = callStore.registerIncomingCall(
                    fromUserId = call.peerUserId,
                    sdp = remoteDescription.sdp,
                    type = call.type,
                    roomId = call.roomId,
                )

                if (!registered) {
                    return
                }

                callStore.savePendingIceCandidates(
                    candidates = result.data.pendingIceCandidates,
                )

                // later:
                // soundPlayer.playRinging()
                // telecomAdapter.showIncomingCall(...)
                // foreground notification
            }

            is ApiResult.Error -> {
                Log.d(TAG, "Failed to restore current ringing call: ${result.error}")
            }
        }
    }

    suspend fun startOutgoingCall(
        targetUserId: String,
        type: com.callover.android.core.domain.models.CallType,
    ) {
        if (callStore.isBusy) {
            return
        }

        // later:
        // request media permissions before calling this or via PermissionCoordinator
        // webRtcEngine.startLocalMedia(...)
        // webRtcEngine.createPeerConnection(...)
        // offer = webRtcEngine.createOffer(...)

        val dummySdp = ""

        val registered = callStore.registerOutgoingCall(
            toUserId = targetUserId,
            sdp = dummySdp,
            type = type,
        )

        if (!registered) {
            return
        }

        // later send real SDP offer:
        // signalingService.sendOffer(targetUserId, offer.description, type)
    }

    suspend fun acceptCall() {
        val incoming = callStore.markIncomingAccepted() ?: return

        // later:
        // start local media
        // create peer connection
        // set remote offer: incoming.sdp
        // drain pending ICE from incoming.fromUserId
        // create answer
        // set local answer
        // signalingService.sendAnswer(incoming.fromUserId, answer.description)

        callStore.markActive(
            peerUserId = incoming.fromUserId,
            type = incoming.type,
            roomId = incoming.roomId,
        )
    }

    suspend fun declineCall() {
        val incoming = callStore.currentState as? CallState.Incoming ?: return

        signalingService.sendDecline(
            toUserId = incoming.fromUserId,
        )

        callStore.setEnded(CallEndReason.Declined)

        // later:
        // soundPlayer.playCancelled()
        // telecomAdapter.disconnect()
        // delay/reset maybe
        callStore.reset()
    }

    suspend fun cancelOutgoingCall() {
        val outgoing = callStore.currentState as? CallState.Outgoing ?: return

        signalingService.sendCancel(
            toUserId = outgoing.toUserId,
        )

        callStore.setEnded(CallEndReason.Cancelled)

        // later:
        // webRtcEngine.close()
        callStore.reset()
    }

    suspend fun endCurrentCall() {
        val peerUserId = when (val state = callStore.currentState) {
            is CallState.Active -> state.peerUserId
            is CallState.Connecting -> state.peerUserId
            is CallState.Incoming -> state.fromUserId
            is CallState.Outgoing -> state.toUserId
            else -> return
        }

        signalingService.sendEnd(
            toUserId = peerUserId,
        )

        callStore.setEnded(CallEndReason.Local)

        // later:
        // webRtcEngine.close()
        // foregroundService.stop()
        // telecomAdapter.disconnect()
        callStore.reset()
    }

    private suspend fun handleSignalingEvent(
        event: SignalingEvent,
    ) {
        when (event) {
            is SignalingEvent.CallOffer -> handleCallOffer(event)
            is SignalingEvent.CallAnswer -> handleCallAnswer(event)
            is SignalingEvent.CallDecline -> handleCallDecline(event)
            is SignalingEvent.CallCancel -> handleCallCancel(event)
            is SignalingEvent.CallEnd -> handleCallEnd(event)
            is SignalingEvent.CallTimeout -> handleCallTimeout(event)
            is SignalingEvent.CallIceCandidate -> handleIceCandidate(event)
        }
    }

    private suspend fun handleCallOffer(
        event: SignalingEvent.CallOffer,
    ) {
        if (callStore.isBusy) {
            signalingService.sendDecline(event.fromUserId)
            return
        }

        callStore.registerIncomingCall(
            fromUserId = event.fromUserId,
            sdp = event.sdp,
            type = event.type,
        )

        // later:
        // soundPlayer.playRinging()
        // telecomAdapter.showIncomingCall(...)
    }

    private fun handleCallAnswer(
        event: SignalingEvent.CallAnswer,
    ) {
        val outgoing = callStore.markOutgoingAccepted(
            fromUserId = event.fromUserId,
            sdp = event.sdp,
        ) ?: return

        // later:
        // webRtcEngine.setRemoteDescription(answer)
        // callStore.drainPendingIce(event.fromUserId)
        // soundPlayer.playActive()

        callStore.markActive(
            peerUserId = event.fromUserId,
            type = outgoing.type,
            roomId = outgoing.roomId,
        )
    }

    private fun handleCallDecline(
        event: SignalingEvent.CallDecline,
    ) {
        val outgoing = callStore.currentState as? CallState.Outgoing ?: return

        if (outgoing.toUserId != event.fromUserId) {
            return
        }

        callStore.setEnded(CallEndReason.Declined)

        // later webRtcEngine.close()
        callStore.reset()
    }

    private fun handleCallCancel(
        event: SignalingEvent.CallCancel,
    ) {
        val incoming = callStore.currentState as? CallState.Incoming ?: return

        if (incoming.fromUserId != event.fromUserId) {
            return
        }

        callStore.setEnded(CallEndReason.Cancelled)

        // later stop ringtone
        callStore.reset()
    }

    private fun handleCallEnd(
        event: SignalingEvent.CallEnd,
    ) {
        val currentPeerUserId = when (val state = callStore.currentState) {
            is CallState.Active -> state.peerUserId
            is CallState.Connecting -> state.peerUserId
            is CallState.Incoming -> state.fromUserId
            is CallState.Outgoing -> state.toUserId
            else -> return
        }

        if (currentPeerUserId != event.fromUserId) {
            return
        }

        callStore.setEnded(CallEndReason.Remote)

        // later webRtcEngine.close()
        callStore.reset()
    }

    private fun handleCallTimeout(
        event: SignalingEvent.CallTimeout,
    ) {
        callStore.setEnded(CallEndReason.Timeout)

        // later webRtcEngine.close()
        callStore.reset()
    }

    private fun handleIceCandidate(
        event: SignalingEvent.CallIceCandidate,
    ) {
        val currentPeerUserId = when (val state = callStore.currentState) {
            is CallState.Active -> state.peerUserId
            is CallState.Connecting -> state.peerUserId
            is CallState.Incoming -> state.fromUserId
            is CallState.Outgoing -> state.toUserId
            else -> null
        }

        if (currentPeerUserId != event.fromUserId) {
            callStore.savePendingIce(
                candidate = com.callover.android.core.domain.models.PendingIceCandidate(
                    fromUserId = event.fromUserId,
                    sdp = event.sdp,
                    sdpMLineIndex = event.sdpMLineIndex,
                    sdpMid = event.sdpMid,
                ),
            )
            return
        }

        // later:
        // if peerConnection ready && remoteDescription set:
        //     webRtcEngine.addIceCandidate(...)
        // else:
        //     callStore.savePendingIce(...)
        callStore.savePendingIce(
            candidate = com.callover.android.core.domain.models.PendingIceCandidate(
                fromUserId = event.fromUserId,
                sdp = event.sdp,
                sdpMLineIndex = event.sdpMLineIndex,
                sdpMid = event.sdpMid,
            ),
        )
    }

    companion object {
        private const val TAG = "CallCoordinator"
    }
}