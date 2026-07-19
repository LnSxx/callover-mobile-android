package com.callover.android.core.webrtc

import com.callover.android.core.domain.models.CallType
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.AudioTrack
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack

interface WebRtcEngine {
    val mediaState: StateFlow<WebRtcMediaState>

    val eglBaseContext: EglBase.Context

    fun createPeerConnection(
        observer: PeerConnection.Observer,
    ): PeerConnection

    fun startLocalMedia(
        callType: CallType,
        peerConnection: PeerConnection,
    ): LocalMedia

    suspend fun createOffer(
        peerConnection: PeerConnection,
    ): SessionDescription

    suspend fun createAnswer(
        peerConnection: PeerConnection,
    ): SessionDescription

    suspend fun setLocalDescription(
        peerConnection: PeerConnection,
        description: SessionDescription,
    )

    suspend fun setRemoteDescription(
        peerConnection: PeerConnection,
        description: SessionDescription,
    )

    fun addIceCandidate(
        peerConnection: PeerConnection,
        candidate: IceCandidate,
    ): Boolean

    fun onRemoteAudioTrack(track: AudioTrack)

    fun onRemoteVideoTrack(track: VideoTrack)

    fun setMicrophoneEnabled(enabled: Boolean)

    fun setCameraEnabled(enabled: Boolean)

    fun release()
}