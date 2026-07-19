package com.callover.android.core.calls

import com.callover.android.core.domain.models.CallDirection
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
        if (isBusy) return false

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
        if (isBusy) return false

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
            direction = CallDirection.Incoming,
            isMicEnabled = true,
            isCameraEnabled = incoming.type == CallType.Video,
        )

        return incoming
    }

    fun getOutgoingCallForAnswer(
        fromUserId: String,
    ): CallState.Outgoing? {
        val outgoing = _state.value as? CallState.Outgoing ?: return null

        if (outgoing.toUserId != fromUserId) {
            return null
        }

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

    fun savePendingIceCandidates(
        candidates: List<PendingIceCandidate>,
    ) {
        candidates.forEach(::savePendingIce)
    }

    fun drainPendingIce(
        fromUserId: String,
    ): List<PendingIceCandidate> {
        return pendingIceByUserId.remove(fromUserId).orEmpty()
    }

    fun setMicEnabled(enabled: Boolean) {
        _state.value = when (val state = _state.value) {
            is CallState.Connecting -> {
                state.copy(isMicEnabled = enabled)
            }

            is CallState.Active -> {
                state.copy(isMicEnabled = enabled)
            }

            else -> state
        }
    }

    fun setCameraEnabled(enabled: Boolean) {
        _state.value = when (val state = _state.value) {
            is CallState.Connecting -> {
                state.copy(isCameraEnabled = enabled)
            }

            is CallState.Active -> {
                state.copy(isCameraEnabled = enabled)
            }

            else -> state
        }
    }

    fun currentMicEnabledOrNull(): Boolean? {
        return when (val state = _state.value) {
            is CallState.Connecting -> state.isMicEnabled
            is CallState.Active -> state.isMicEnabled
            else -> null
        }
    }

    fun currentCameraEnabledOrNull(): Boolean? {
        return when (val state = _state.value) {
            is CallState.Connecting -> state.isCameraEnabled
            is CallState.Active -> state.isCameraEnabled
            else -> null
        }
    }
}