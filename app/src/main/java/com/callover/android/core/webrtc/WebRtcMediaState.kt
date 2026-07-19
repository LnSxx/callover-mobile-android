package com.callover.android.core.webrtc

import org.webrtc.VideoTrack

data class WebRtcMediaState(
    val hasRemoteAudioTrack: Boolean = false,
    val localVideoTrack: VideoTrack? = null,
    val remoteVideoTrack: VideoTrack? = null,
)