package com.callover.android.features.call_overlay_host

import com.callover.android.core.calls.CallState

data class CallUiState(
    val callState: CallState = CallState.Idle,
    val peerDisplayName: String = "",
    val peerUserId: String? = null,
)