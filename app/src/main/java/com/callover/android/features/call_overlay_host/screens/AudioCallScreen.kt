package com.callover.android.features.call_overlay_host.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.callover.android.R
import com.callover.android.core.calls.CallState
import com.callover.android.core.domain.models.CallDirection
import com.callover.android.core.domain.models.CallType
import com.callover.android.features.call_overlay_host.components.CallStatusText
import com.callover.android.ui.components.CallCircleButton
import com.callover.android.ui.theme.CalloverMobileTheme
import java.time.Instant

@Composable
fun AudioCallScreen(
    callState: CallState,
    contactName: String,
    onEndClick: () -> Unit,
    onToggleMicClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isMicEnabled = when (callState) {
        is CallState.Active -> callState.isMicEnabled
        is CallState.Connecting -> callState.isMicEnabled
        else -> true
    }

    val peerUserId = when (callState) {
        is CallState.Active -> callState.peerUserId
        is CallState.Connecting -> callState.peerUserId
        else -> ""
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = contactName.ifBlank { peerUserId },
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )

                Spacer(modifier = Modifier.height(8.dp))

                CallStatusText(
                    callState = callState,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CallCircleButton(
                    icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                    label = stringResource(R.string.microphone),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = onToggleMicClick,
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

@Preview(
    name = "Audio call active",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun AudioCallScreenPreview() {
    CalloverMobileTheme {
        AudioCallScreen(
            callState = CallState.Active(
                peerUserId = "user-id",
                type = CallType.Audio,
                roomId = "room-id",
                startedAt = Instant.now(),
                isMicEnabled = true,
                isCameraEnabled = false,
            ),
            contactName = "Catherine the Great",
            onEndClick = {  },
            onToggleMicClick = {  },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "Audio call connecting",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun AudioCallScreenConnectingPreview() {
    CalloverMobileTheme {
        AudioCallScreen(
            callState = CallState.Connecting(
                peerUserId = "user-id",
                type = CallType.Audio,
                roomId = "room-id",
                direction = CallDirection.Incoming,
                isMicEnabled = true,
                isCameraEnabled = false,
            ),
            contactName = "Catherine the Great Empress of all Russia and other other other...",
            onEndClick = {  },
            onToggleMicClick = {  },
            modifier = Modifier.fillMaxSize(),
        )
    }
}