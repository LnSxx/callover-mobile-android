package com.callover.android.features.call_overlay_host

import com.callover.android.core.calls.CallState
import com.callover.android.core.webrtc.WebRtcMediaState

data class CallUiState(
    val callState: CallState = CallState.Idle,
    val peerDisplayName: String = "",
    val peerUserId: String? = null,
    val mediaState: WebRtcMediaState = WebRtcMediaState(),
)