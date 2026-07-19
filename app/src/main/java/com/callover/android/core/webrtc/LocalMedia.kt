package com.callover.android.core.webrtc

import org.webrtc.AudioTrack
import org.webrtc.VideoTrack

data class LocalMedia(
    val audioTrack: AudioTrack,
    val videoTrack: VideoTrack?,
)