package com.callover.android.features.call_overlay_host.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

@Composable
fun WebRtcVideoRenderer(
    videoTrack: VideoTrack?,
    eglBaseContext: EglBase.Context,
    modifier: Modifier = Modifier,
    mirror: Boolean = false,
) {
    val context = LocalContext.current

    val renderer = remember {
        SurfaceViewRenderer(context).apply {
            init(eglBaseContext, null)
            setMirror(mirror)
            setEnableHardwareScaler(true)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)

            Log.d(
                TAG,
                "renderer created mirror=$mirror renderer=$this",
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            renderer
        },
    )

    DisposableEffect(videoTrack, renderer) {
        val trackId = try {
            videoTrack?.id()
        } catch (_: Throwable) {
            null
        }

        if (videoTrack != null) {
            try {
                Log.d(TAG, "addSink trackId=$trackId renderer=$renderer")
                videoTrack.addSink(renderer)
            } catch (error: Throwable) {
                Log.d(TAG, "Failed to addSink trackId=$trackId", error)
            }
        }

        onDispose {
            if (videoTrack != null) {
                try {
                    Log.d(TAG, "removeSink trackId=$trackId renderer=$renderer")
                    videoTrack.removeSink(renderer)
                } catch (error: Throwable) {
                    Log.d(
                        TAG,
                        "Failed to removeSink trackId=$trackId; track was probably already disposed",
                        error,
                    )
                }
            }
        }
    }

    DisposableEffect(renderer) {
        onDispose {
            try {
                Log.d(TAG, "renderer release renderer=$renderer")
                renderer.release()
            } catch (error: Throwable) {
                Log.d(TAG, "Failed to release renderer", error)
            }
        }
    }
}

private const val TAG = "WebRtcVideoRenderer"