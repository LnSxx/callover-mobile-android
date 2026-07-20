package com.callover.android.features.call_overlay_host.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
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
import com.callover.android.core.domain.models.CallType
import com.callover.android.ui.components.CallCircleButton
import com.callover.android.ui.theme.CalloverMobileTheme

@Composable
fun OutgoingCallScreen(
    callState: CallState.Outgoing,
    contactName: String,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = contactName.ifBlank { callState.toUserId },
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.calling_three_dots),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            CallCircleButton(
                icon = Icons.Default.CallEnd,
                label = stringResource(R.string.cancel),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                onClick = onCancelClick,
            )
        }
    }
}

@Preview
@Composable
private fun IncomingCallScreenPreview() {
    CalloverMobileTheme {
        OutgoingCallScreen(
            callState = CallState.Outgoing(
                toUserId = "user-id",
                sdp = "sdp",
                type = CallType.Video,
                roomId = "room-id"
            ),
            contactName = "Someone",
            onCancelClick = {  },
        )
    }
}