package com.callover.android.features.call_overlay_host.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.callover.android.R
import com.callover.android.core.calls.CallState
import com.callover.android.features.call_overlay_host.components.WebRtcVideoRenderer
import com.callover.android.ui.components.CallCircleButton
import org.webrtc.EglBase
import org.webrtc.VideoTrack

@Composable
fun VideoCallScreen(
    callState: CallState,
    contactName: String,
    remoteVideoTrack: VideoTrack?,
    localVideoTrack: VideoTrack?,
    eglBaseContext: EglBase.Context,
    onEndClick: () -> Unit,
    onToggleMicClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMicEnabled = when (callState) {
        is CallState.Active -> callState.isMicEnabled
        is CallState.Connecting -> callState.isMicEnabled
        else -> true
    }

    val isCameraEnabled = when (callState) {
        is CallState.Active -> callState.isCameraEnabled
        is CallState.Connecting -> callState.isCameraEnabled
        else -> true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        WebRtcVideoRenderer(
            videoTrack = remoteVideoTrack,
            eglBaseContext = eglBaseContext,
            modifier = Modifier.fillMaxSize(),
            mirror = false,
        )

        WebRtcVideoRenderer(
            videoTrack = localVideoTrack,
            eglBaseContext = eglBaseContext,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(width = 120.dp, height = 180.dp),
            mirror = true,
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CallCircleButton(
                icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                label = stringResource(R.string.microphone),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onToggleMicClick,
            )

            CallCircleButton(
                icon = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                label = stringResource(R.string.camera),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onToggleCameraClick,
            )

            CallCircleButton(
                icon = Icons.Default.CallEnd,
                label = stringResource(R.string.end_call),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                onClick = onEndClick,
            )
        }
    }
}