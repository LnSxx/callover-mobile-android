package com.callover.android.core.call_coordinator

import android.util.Log
import com.callover.android.core.calls.CallEndReason
import com.callover.android.core.calls.CallState
import com.callover.android.core.calls.CallStore
import com.callover.android.core.data.calls.CallsRepository
import com.callover.android.core.domain.models.CallStatus
import com.callover.android.core.domain.models.CallType
import com.callover.android.core.domain.models.PendingIceCandidate
import com.callover.android.core.domain.models.RemoteDescriptionType
import com.callover.android.core.network.ApiResult
import com.callover.android.core.realtime.signaling.SignalingEvent
import com.callover.android.core.realtime.signaling.SignalingRealtimeDataSource
import com.callover.android.core.signaling.SignalingService
import com.callover.android.core.webrtc.WebRtcEngine
import com.callover.android.core.webrtc.toRtcIceCandidate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.webrtc.AudioTrack
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.IceCandidateErrorEvent
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallCoordinator @Inject constructor(
    private val callStore: CallStore,
    private val callsRepository: CallsRepository,
    private val signalingRealtimeDataSource: SignalingRealtimeDataSource,
    private val signalingService: SignalingService,
    private val webRtcEngine: WebRtcEngine,
) {
    val callState: StateFlow<CallState> = callStore.state

    private var peerConnection: PeerConnection? = null

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO,
    )

    private val cleanupMutex = Mutex()
    private var isCleaningUp = false

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
        type: CallType,
    ) {
        if (callStore.isBusy) return

        try {
            val peerConnection = webRtcEngine.createPeerConnection(
                observer = createPeerConnectionObserver(targetUserId),
            )

            this.peerConnection = peerConnection

            webRtcEngine.startLocalMedia(
                callType = type,
                peerConnection = peerConnection,
            )

            val offer = webRtcEngine.createOffer(peerConnection)

            webRtcEngine.setLocalDescription(
                peerConnection = peerConnection,
                description = offer,
            )

            val registered = callStore.registerOutgoingCall(
                toUserId = targetUserId,
                sdp = offer.description,
                type = type,
            )

            if (!registered) {
                cleanupCurrentCall()
                return
            }

            signalingService.sendOffer(
                toUserId = targetUserId,
                sdp = offer.description,
                type = type,
            )
        } catch (error: Throwable) {
            Log.d(TAG, "Failed to start outgoing call", error)
            failCurrentCall(error)
        }
    }

    suspend fun acceptCall() {
        val incoming = callStore.markIncomingAccepted() ?: return

        try {
            val peerConnection = webRtcEngine.createPeerConnection(
                observer = createPeerConnectionObserver(
                    peerUserId = incoming.fromUserId,
                ),
            )

            this.peerConnection = peerConnection

            webRtcEngine.startLocalMedia(
                callType = incoming.type,
                peerConnection = peerConnection,
            )

            val remoteOffer = SessionDescription(
                SessionDescription.Type.OFFER,
                incoming.sdp,
            )

            webRtcEngine.setRemoteDescription(
                peerConnection = peerConnection,
                description = remoteOffer,
            )

            callStore.drainPendingIce(
                fromUserId = incoming.fromUserId,
            ).forEach { candidate ->
                webRtcEngine.addIceCandidate(
                    peerConnection = peerConnection,
                    candidate = candidate.toRtcIceCandidate(),
                )
            }

            val answer = webRtcEngine.createAnswer(
                peerConnection = peerConnection,
            )

            webRtcEngine.setLocalDescription(
                peerConnection = peerConnection,
                description = answer,
            )

            when (
                val result = signalingService.sendAnswer(
                    toUserId = incoming.fromUserId,
                    sdp = answer.description,
                )
            ) {
                is ApiResult.Success -> {
                    callStore.markActive(
                        peerUserId = incoming.fromUserId,
                        type = incoming.type,
                        roomId = incoming.roomId,
                    )
                }

                is ApiResult.Error -> {
                    Log.d(TAG, "Failed to send call answer: ${result.error}")
                    failCurrentCall()
                }
            }
        } catch (error: Throwable) {
            failCurrentCall(error)
        }
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
        cleanupCurrentCall()
    }

    suspend fun cancelOutgoingCall() {
        val peerUserId = when (val state = callStore.currentState) {
            is CallState.Outgoing -> state.toUserId
            is CallState.Connecting -> state.peerUserId
            else -> return
        }

        signalingService.sendCancel(
            toUserId = peerUserId,
        )

        callStore.setEnded(CallEndReason.Cancelled)

        cleanupCurrentCall()
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
        // foregroundService.stop()
        // telecomAdapter.disconnect()
        cleanupCurrentCall()
    }

    fun toggleMic() {
        val current = callStore.currentMicEnabledOrNull() ?: return
        val next = !current

        webRtcEngine.setMicrophoneEnabled(next)
        callStore.setMicEnabled(next)
    }

    fun toggleCamera() {
        val state = callStore.currentState

        val callType = when (state) {
            is CallState.Connecting -> state.type
            is CallState.Active -> state.type
            else -> return
        }

        if (callType != CallType.Video) {
            return
        }

        val current = callStore.currentCameraEnabledOrNull() ?: return
        val next = !current

        webRtcEngine.setCameraEnabled(next)
        callStore.setCameraEnabled(next)
    }

    suspend fun rejectBecauseMediaPermissionDenied() {
        when (val state = callStore.currentState) {
            is CallState.Incoming -> {
                signalingService.sendDecline(
                    toUserId = state.fromUserId,
                )

                callStore.setEnded(CallEndReason.PermissionDenied)
                cleanupCurrentCall()
            }

            is CallState.Outgoing,
            is CallState.Connecting,
            is CallState.Active -> {
                endCurrentCall()
            }

            else -> Unit
        }
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
        Log.d(
            TAG,
            "call.offer hasVideo=${event.sdp.contains("m=video")} hasAudio=${event.sdp.contains("m=audio")}",
        )

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

    private suspend fun handleCallAnswer(
        event: SignalingEvent.CallAnswer,
    ) {
        Log.d(
            TAG,
            "call.answer hasVideo=${event.sdp.contains("m=video")} hasAudio=${event.sdp.contains("m=audio")}",
        )

        val outgoing = callStore.getOutgoingCallForAnswer(
            fromUserId = event.fromUserId,
        ) ?: return

        val currentPeerConnection = peerConnection
        if (currentPeerConnection == null) {
            Log.d(TAG, "Ignoring call.answer: peerConnection is null")
            failCurrentCall()
            return
        }

        try {
            val remoteAnswer = SessionDescription(
                SessionDescription.Type.ANSWER,
                event.sdp,
            )

            webRtcEngine.setRemoteDescription(
                peerConnection = currentPeerConnection,
                description = remoteAnswer,
            )

            callStore.drainPendingIce(
                fromUserId = event.fromUserId,
            ).forEach { candidate ->
                val added = webRtcEngine.addIceCandidate(
                    peerConnection = currentPeerConnection,
                    candidate = candidate.toRtcIceCandidate(),
                )

                if (!added) {
                    Log.d(TAG, "Failed to add pending ICE candidate after answer")
                }
            }

            callStore.markActive(
                peerUserId = event.fromUserId,
                type = outgoing.type,
                roomId = outgoing.roomId,
            )
        } catch (error: Throwable) {
            Log.d(TAG, "Failed to handle call.answer", error)
            failCurrentCall(error)
        }
    }

    private suspend fun handleCallDecline(
        event: SignalingEvent.CallDecline,
    ) {
        val outgoing = callStore.currentState as? CallState.Outgoing ?: return

        if (outgoing.toUserId != event.fromUserId) {
            return
        }

        callStore.setEnded(CallEndReason.Declined)

        cleanupCurrentCall()
    }

    private suspend fun handleCallCancel(
        event: SignalingEvent.CallCancel,
    ) {
        val peerUserId = when (val state = callStore.currentState) {
            is CallState.Incoming -> state.fromUserId
            is CallState.Connecting -> state.peerUserId
            else -> return
        }

        if (peerUserId != event.fromUserId) {
            return
        }

        callStore.setEnded(CallEndReason.Cancelled)
        cleanupCurrentCall()
    }

    private suspend fun handleCallEnd(
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

        cleanupCurrentCall()
    }

    private suspend fun handleCallTimeout(
        event: SignalingEvent.CallTimeout,
    ) {
        callStore.setEnded(CallEndReason.Timeout)

        cleanupCurrentCall()
    }

    private fun handleIceCandidate(
        event: SignalingEvent.CallIceCandidate,
    ) {
        val candidate = PendingIceCandidate(
            fromUserId = event.fromUserId,
            sdp = event.sdp,
            sdpMLineIndex = event.sdpMLineIndex,
            sdpMid = event.sdpMid,
        )

        val currentPeerUserId = when (val state = callStore.currentState) {
            is CallState.Active -> state.peerUserId
            is CallState.Connecting -> state.peerUserId
            is CallState.Incoming -> state.fromUserId
            is CallState.Outgoing -> state.toUserId
            else -> null
        }

        if (currentPeerUserId != event.fromUserId) {
            callStore.savePendingIce(candidate)
            return
        }

        val currentPeerConnection = peerConnection

        if (currentPeerConnection == null || currentPeerConnection.remoteDescription == null) {
            callStore.savePendingIce(candidate)
            return
        }

        val added = webRtcEngine.addIceCandidate(
            peerConnection = currentPeerConnection,
            candidate = candidate.toRtcIceCandidate(),
        )

        if (!added) {
            callStore.savePendingIce(candidate)
        }
    }

    private suspend fun failCurrentCall(
        error: Throwable? = null,
    ) {
        if (error != null) {
            Log.d(TAG, "Call failed", error)
        }

        callStore.setEnded(CallEndReason.Failed)
        cleanupCurrentCall()
    }

    private suspend fun cleanupCurrentCall() {
        cleanupMutex.withLock {
            if (isCleaningUp) {
                return
            }

            isCleaningUp = true

            try {
                peerConnection?.close()
                peerConnection?.dispose()
                peerConnection = null

                webRtcEngine.release()
                callStore.reset()
            } finally {
                isCleaningUp = false
            }
        }
    }

    private fun createPeerConnectionObserver(
        peerUserId: String,
    ): PeerConnection.Observer {
        return object : PeerConnection.Observer {
            override fun onIceCandidate(candidate: IceCandidate) {
                scope.launch {
                    signalingService.sendIceCandidate(
                        toUserId = peerUserId,
                        sdp = candidate.sdp,
                        sdpMLineIndex = candidate.sdpMLineIndex,
                        sdpMid = candidate.sdpMid,
                    )
                }
            }

            override fun onAddTrack(
                receiver: RtpReceiver?,
                mediaStreams: Array<out MediaStream>?,
            ) {
                val track = receiver?.track()

                Log.d(
                    TAG,
                    "onAddTrack receiver=$receiver track=$track kind=${track?.kind()} id=${track?.id()} state=${track?.state()} streams=${mediaStreams?.size}",
                )

                when (track) {
                    is AudioTrack -> {
                        Log.d(TAG, "Remote audio track received through onAddTrack")
                        webRtcEngine.onRemoteAudioTrack(track)
                    }

                    is VideoTrack -> {
                        Log.d(TAG, "Remote video track received through onAddTrack")
                        webRtcEngine.onRemoteVideoTrack(track)
                    }

                    else -> {
                        Log.d(TAG, "Unknown remote track in onAddTrack: ${track?.javaClass?.name}")
                    }
                }
            }

            override fun onAddStream(stream: MediaStream?) {
                Log.d(
                    TAG,
                    "onAddStream audio=${stream?.audioTracks?.size} video=${stream?.videoTracks?.size}",
                )

                stream?.audioTracks?.forEach { track ->
                    Log.d(TAG, "Remote audio track received through onAddStream id=${track.id()}")
                    webRtcEngine.onRemoteAudioTrack(track)
                }

                stream?.videoTracks?.forEach { track ->
                    Log.d(TAG, "Remote video track received through onAddStream id=${track.id()}")
                    webRtcEngine.onRemoteVideoTrack(track)
                }
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) = Unit
            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) = Unit
            override fun onSignalingChange(state: PeerConnection.SignalingState?) = Unit
            override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) = Unit
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) = Unit
            override fun onIceCandidateError(event: IceCandidateErrorEvent?) = Unit
            override fun onRemoveStream(stream: MediaStream?) = Unit
            override fun onDataChannel(dataChannel: DataChannel?) = Unit
            override fun onRenegotiationNeeded() = Unit
        }
    }

    companion object {
        private const val TAG = "CallCoordinator"
    }
}