package com.callover.android.core.calls

import com.callover.android.core.domain.models.CallType
import com.callover.android.core.domain.models.PendingIceCandidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallStore @Inject constructor() {
    private val _state = MutableStateFlow<CallState>(CallState.Idle)
    val state: StateFlow<CallState> = _state.asStateFlow()

    private val pendingIceByUserId = mutableMapOf<String, MutableList<PendingIceCandidate>>()

    val currentState: CallState
        get() = _state.value

    val isBusy: Boolean
        get() = _state.value !is CallState.Idle

    fun registerIncomingCall(
        fromUserId: String,
        sdp: String,
        type: CallType,
        roomId: String? = null,
    ): Boolean {
        if (isBusy) {
            return false
        }

        _state.value = CallState.Incoming(
            fromUserId = fromUserId,
            sdp = sdp,
            type = type,
            roomId = roomId,
        )

        return true
    }

    fun registerOutgoingCall(
        toUserId: String,
        sdp: String,
        type: CallType,
        roomId: String? = null,
    ): Boolean {
        if (isBusy) {
            return false
        }

        _state.value = CallState.Outgoing(
            toUserId = toUserId,
            sdp = sdp,
            type = type,
            roomId = roomId,
        )

        return true
    }

    fun markIncomingAccepted(): CallState.Incoming? {
        val incoming = _state.value as? CallState.Incoming ?: return null

        _state.value = CallState.Connecting(
            peerUserId = incoming.fromUserId,
            type = incoming.type,
            roomId = incoming.roomId,
            isMicEnabled = true,
            isCameraEnabled = true,
        )

        return incoming
    }

    fun markOutgoingAccepted(
        fromUserId: String,
        sdp: String,
    ): CallState.Outgoing? {
        val outgoing = _state.value as? CallState.Outgoing ?: return null

        if (outgoing.toUserId != fromUserId) {
            return null
        }

        _state.value = CallState.Active(
            peerUserId = fromUserId,
            type = outgoing.type,
            roomId = outgoing.roomId,
            isMicEnabled = true,
            isCameraEnabled = outgoing.type == CallType.Video,
        )

        return outgoing
    }

    fun markActive(
        peerUserId: String,
        type: CallType,
        roomId: String?,
    ) {
        _state.value = CallState.Active(
            peerUserId = peerUserId,
            type = type,
            roomId = roomId,
            isMicEnabled = true,
            isCameraEnabled = type == CallType.Video,
        )
    }

    fun setEnded(reason: CallEndReason) {
        _state.value = CallState.Ended(reason)
    }

    fun reset() {
        _state.value = CallState.Idle
        pendingIceByUserId.clear()
    }

    fun savePendingIce(
        candidate: PendingIceCandidate,
    ) {
        pendingIceByUserId
            .getOrPut(candidate.fromUserId) { mutableListOf() }
            .add(candidate)
    }

    fun drainPendingIce(
        fromUserId: String,
    ): List<PendingIceCandidate> {
        return pendingIceByUserId.remove(fromUserId).orEmpty()
    }

    fun savePendingIceCandidates(
        candidates: List<PendingIceCandidate>,
    ) {
        candidates.forEach { candidate ->
            savePendingIce(candidate)
        }
    }
}