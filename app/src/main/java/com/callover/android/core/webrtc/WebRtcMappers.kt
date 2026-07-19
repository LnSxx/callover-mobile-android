package com.callover.android.core.webrtc

import com.callover.android.core.domain.models.PendingIceCandidate
import org.webrtc.IceCandidate

fun PendingIceCandidate.toRtcIceCandidate(): IceCandidate {
    return IceCandidate(
        sdpMid,
        sdpMLineIndex,
        sdp,
    )
}