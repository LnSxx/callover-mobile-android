package com.callover.android.core.webrtc

import android.content.Context
import android.util.Log
import com.callover.android.core.domain.models.CallType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoSource
import javax.inject.Inject
import javax.inject.Singleton
import org.webrtc.VideoTrack

@Singleton
class WebRtcEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : WebRtcEngine {

    private val eglBase: EglBase = EglBase.create()

    override val eglBaseContext: EglBase.Context
        get() = eglBase.eglBaseContext

    private var peerConnectionFactory: PeerConnectionFactory? = null

    private var audioSource: AudioSource? = null
    private var videoSource: VideoSource? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    private var cameraCapturer: CameraVideoCapturer? = null

    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null

    private var remoteAudioTrack: AudioTrack? = null
    private var remoteVideoTrack: VideoTrack? = null

    private val _mediaState = MutableStateFlow(WebRtcMediaState())
    override val mediaState: StateFlow<WebRtcMediaState> = _mediaState.asStateFlow()

    private fun initialize() {
        if (peerConnectionFactory != null) return

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions(),
        )

        val encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext,
            true,
            true,
        )

        val decoderFactory = DefaultVideoDecoderFactory(
            eglBase.eglBaseContext,
        )

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    override fun createPeerConnection(
        observer: PeerConnection.Observer,
    ): PeerConnection {
        initialize()

        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                .createIceServer(),
        )

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

        return requireNotNull(peerConnectionFactory)
            .createPeerConnection(rtcConfig, observer)
            ?: error("Failed to create PeerConnection")
    }

    override fun startLocalMedia(
        callType: CallType,
        peerConnection: PeerConnection,
    ): LocalMedia {
        initialize()

        val factory = requireNotNull(peerConnectionFactory)

        val newAudioSource = factory.createAudioSource(MediaConstraints())
        val newAudioTrack = factory.createAudioTrack(
            AUDIO_TRACK_ID,
            newAudioSource,
        )

        audioSource = newAudioSource
        localAudioTrack = newAudioTrack
        newAudioTrack.setEnabled(true)

        peerConnection.addTrack(
            newAudioTrack,
            listOf(LOCAL_STREAM_ID),
        )

        val newVideoTrack = if (callType == CallType.Video) {
            createLocalVideoTrack(
                factory = factory,
                peerConnection = peerConnection,
            )
        } else {
            null
        }

        localVideoTrack = newVideoTrack

        _mediaState.value = _mediaState.value.copy(
            localVideoTrack = newVideoTrack,
        )

        Log.d(
            TAG,
            "localVideoTrack created=${newVideoTrack != null} id=${newVideoTrack?.id()}",
        )

        return LocalMedia(
            audioTrack = newAudioTrack,
            videoTrack = newVideoTrack,
        )
    }

    private fun createLocalVideoTrack(
        factory: PeerConnectionFactory,
        peerConnection: PeerConnection,
    ): VideoTrack {
        val newVideoSource = factory.createVideoSource(false)
        val newSurfaceTextureHelper = SurfaceTextureHelper.create(
            CAMERA_THREAD_NAME,
            eglBase.eglBaseContext,
        )

        val newCameraCapturer = createCameraCapturer()
            ?: error("Failed to create camera capturer")

        newCameraCapturer.initialize(
            newSurfaceTextureHelper,
            context,
            newVideoSource.capturerObserver,
        )

        newCameraCapturer.startCapture(
            VIDEO_WIDTH,
            VIDEO_HEIGHT,
            VIDEO_FPS,
        )

        val newVideoTrack = factory.createVideoTrack(
            VIDEO_TRACK_ID,
            newVideoSource,
        )

        newVideoTrack.setEnabled(true)

        videoSource = newVideoSource
        surfaceTextureHelper = newSurfaceTextureHelper
        cameraCapturer = newCameraCapturer

        peerConnection.addTrack(
            newVideoTrack,
            listOf(LOCAL_STREAM_ID),
        )

        return newVideoTrack
    }

    private fun createCameraCapturer(): CameraVideoCapturer? {
        val enumerator = createCameraEnumerator()

        val frontCameraNames = enumerator.deviceNames.filter { name ->
            enumerator.isFrontFacing(name)
        }

        for (name in frontCameraNames) {
            val capturer = enumerator.createCapturer(name, null)
            if (capturer != null) {
                return capturer
            }
        }

        val otherCameraNames = enumerator.deviceNames.filterNot { name ->
            enumerator.isFrontFacing(name)
        }

        for (name in otherCameraNames) {
            val capturer = enumerator.createCapturer(name, null)
            if (capturer != null) {
                return capturer
            }
        }

        return null
    }

    private fun createCameraEnumerator(): CameraEnumerator {
        return if (Camera2Enumerator.isSupported(context)) {
            Camera2Enumerator(context)
        } else {
            Camera1Enumerator(false)
        }
    }

    override fun onRemoteAudioTrack(track: AudioTrack) {
        remoteAudioTrack = track
        track.setEnabled(true)

        _mediaState.value = _mediaState.value.copy(
            hasRemoteAudioTrack = true,
        )
    }

    override fun onRemoteVideoTrack(track: VideoTrack) {
        Log.d(
            TAG,
            "onRemoteVideoTrack id=${track.id()} kind=${track.kind()} state=${track.state()}",
        )

        remoteVideoTrack = track
        track.setEnabled(true)

        _mediaState.value = _mediaState.value.copy(
            remoteVideoTrack = track,
        )
    }

    override suspend fun createOffer(
        peerConnection: PeerConnection,
    ): SessionDescription {
        return peerConnection.createOfferSuspend()
    }

    override suspend fun createAnswer(
        peerConnection: PeerConnection,
    ): SessionDescription {
        return peerConnection.createAnswerSuspend()
    }

    override suspend fun setLocalDescription(
        peerConnection: PeerConnection,
        description: SessionDescription,
    ) {
        peerConnection.setLocalDescriptionSuspend(description)
    }

    override suspend fun setRemoteDescription(
        peerConnection: PeerConnection,
        description: SessionDescription,
    ) {
        peerConnection.setRemoteDescriptionSuspend(description)
    }

    override fun addIceCandidate(
        peerConnection: PeerConnection,
        candidate: IceCandidate,
    ): Boolean {
        return peerConnection.addIceCandidate(candidate)
    }

    override fun setMicrophoneEnabled(enabled: Boolean) {
        localAudioTrack?.setEnabled(enabled)
    }

    override fun setCameraEnabled(enabled: Boolean) {
        localVideoTrack?.setEnabled(enabled)
    }

    override fun release() {
        _mediaState.value = WebRtcMediaState()

        remoteAudioTrack = null
        remoteVideoTrack = null

        try {
            localAudioTrack?.setEnabled(false)
        } catch (_: Throwable) {
        }

        try {
            localVideoTrack?.setEnabled(false)
        } catch (_: Throwable) {
        }

        try {
            cameraCapturer?.stopCapture()
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } catch (_: Throwable) {
        }

        try {
            cameraCapturer?.dispose()
        } catch (_: Throwable) {
        }
        cameraCapturer = null

        try {
            localVideoTrack?.dispose()
        } catch (_: Throwable) {
        }
        localVideoTrack = null

        try {
            videoSource?.dispose()
        } catch (_: Throwable) {
        }
        videoSource = null

        try {
            surfaceTextureHelper?.dispose()
        } catch (_: Throwable) {
        }
        surfaceTextureHelper = null

        try {
            localAudioTrack?.dispose()
        } catch (_: Throwable) {
        }
        localAudioTrack = null

        try {
            audioSource?.dispose()
        } catch (_: Throwable) {
        }
        audioSource = null
    }

    companion object {

        private const val TAG = "WebRTC Engine"
        private const val LOCAL_STREAM_ID = "callover-local-stream"
        private const val AUDIO_TRACK_ID = "callover-audio-track"
        private const val VIDEO_TRACK_ID = "callover-video-track"
        private const val CAMERA_THREAD_NAME = "CalloverCameraThread"

        private const val VIDEO_WIDTH = 1280
        private const val VIDEO_HEIGHT = 720
        private const val VIDEO_FPS = 30
    }
}