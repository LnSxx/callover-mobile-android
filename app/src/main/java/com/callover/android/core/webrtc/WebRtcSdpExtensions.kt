package com.callover.android.core.webrtc

import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun PeerConnection.createOfferSuspend(): SessionDescription {
    return suspendCancellableCoroutine { continuation ->
        createOffer(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription) {
                    continuation.resume(description)
                }

                override fun onSetSuccess() = Unit

                override fun onCreateFailure(error: String) {
                    continuation.resumeWithException(
                        IllegalStateException("createOffer failed: $error")
                    )
                }

                override fun onSetFailure(error: String) = Unit
            },
            MediaConstraints(),
        )
    }
}

suspend fun PeerConnection.createAnswerSuspend(): SessionDescription {
    return suspendCancellableCoroutine { continuation ->
        createAnswer(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription) {
                    continuation.resume(description)
                }

                override fun onSetSuccess() = Unit

                override fun onCreateFailure(error: String) {
                    continuation.resumeWithException(
                        IllegalStateException("createAnswer failed: $error")
                    )
                }

                override fun onSetFailure(error: String) = Unit
            },
            MediaConstraints(),
        )
    }
}

suspend fun PeerConnection.setLocalDescriptionSuspend(
    description: SessionDescription,
) {
    return suspendCancellableCoroutine { continuation ->
        setLocalDescription(
            object : SdpObserver {
                override fun onSetSuccess() {
                    continuation.resume(Unit)
                }

                override fun onCreateSuccess(description: SessionDescription) = Unit

                override fun onCreateFailure(error: String) = Unit

                override fun onSetFailure(error: String) {
                    continuation.resumeWithException(
                        IllegalStateException("setLocalDescription failed: $error")
                    )
                }
            },
            description,
        )
    }
}

suspend fun PeerConnection.setRemoteDescriptionSuspend(
    description: SessionDescription,
) {
    return suspendCancellableCoroutine { continuation ->
        setRemoteDescription(
            object : SdpObserver {
                override fun onSetSuccess() {
                    continuation.resume(Unit)
                }

                override fun onCreateSuccess(description: SessionDescription) = Unit

                override fun onCreateFailure(error: String) = Unit

                override fun onSetFailure(error: String) {
                    continuation.resumeWithException(
                        IllegalStateException("setRemoteDescription failed: $error")
                    )
                }
            },
            description,
        )
    }
}