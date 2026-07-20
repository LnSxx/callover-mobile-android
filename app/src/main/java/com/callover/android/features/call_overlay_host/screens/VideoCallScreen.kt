package com.callover.android.features.call_overlay_host.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.callover.android.R
import com.callover.android.core.calls.CallState
import com.callover.android.core.domain.models.CallType
import com.callover.android.features.call_overlay_host.components.CallStatusText
import com.callover.android.features.call_overlay_host.components.WebRtcVideoRenderer
import com.callover.android.ui.components.CallCircleButton
import com.callover.android.ui.theme.CalloverMobileTheme
import org.webrtc.EglBase
import org.webrtc.VideoTrack
import java.time.Instant

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
    VideoCallScreenContent(
        modifier = modifier,
        callState = callState,
        contactName = contactName,
        remoteVideoContent = {
            WebRtcVideoRenderer(
                videoTrack = remoteVideoTrack,
                eglBaseContext = eglBaseContext,
                modifier = Modifier.fillMaxSize(),
                mirror = false,
            )
        },
        localVideoContent = {
            WebRtcVideoRenderer(
                videoTrack = localVideoTrack,
                eglBaseContext = eglBaseContext,
                modifier = Modifier.fillMaxSize(),
                mirror = true,
            )
        },
        onEndClick = onEndClick,
        onToggleMicClick = onToggleMicClick,
        onToggleCameraClick = onToggleCameraClick,
    )
}

@Composable
private fun VideoCallScreenContent(
    callState: CallState,
    contactName: String,
    remoteVideoContent: @Composable () -> Unit,
    localVideoContent: @Composable () -> Unit,
    onEndClick: () -> Unit,
    onToggleMicClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isLocalVideoLarge by rememberSaveable {
        mutableStateOf(false)
    }

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

    val largeVideoContent = if (isLocalVideoLarge) {
        localVideoContent
    } else {
        remoteVideoContent
    }

    val smallVideoContent = if (isLocalVideoLarge) {
        remoteVideoContent
    } else {
        localVideoContent
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        largeVideoContent()

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 48.dp,
                    end = 24.dp,
                )
                .size(width = 120.dp, height = 180.dp)
                .background(Color.Black)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.35f),
                )
                .clickable {
                    isLocalVideoLarge = !isLocalVideoLarge
                },
        ) {
            smallVideoContent()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CallStatusText(
                callState = callState,
            )

            Row(
                modifier = Modifier
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
}

@Composable
private fun PreviewVideoPlaceholder(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101010)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.65f),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(
    name = "Video call active",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun VideoCallScreenPreview() {
    CalloverMobileTheme {
        VideoCallScreenContent(
            modifier = Modifier.fillMaxSize(),
            callState = CallState.Active(
                peerUserId = "user-id",
                type = CallType.Video,
                roomId = "room-id",
                startedAt = Instant.now(),
                isMicEnabled = true,
                isCameraEnabled = false,
            ),
            contactName = "Ivan IV the Terrible",
            remoteVideoContent = {
                PreviewVideoPlaceholder(
                    text = "Remote video",
                    modifier = Modifier.fillMaxSize(),
                )
            },
            localVideoContent = {
                PreviewVideoPlaceholder(
                    text = "You",
                    modifier = Modifier.fillMaxSize(),
                )
            },
            onEndClick = {},
            onToggleMicClick = {},
            onToggleCameraClick = {},
        )
    }
}