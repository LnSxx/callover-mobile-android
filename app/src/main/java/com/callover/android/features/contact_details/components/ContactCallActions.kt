package com.callover.android.features.contact_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContactCallActions(
    onAudioCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = onAudioCallClick,
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
            )
        }

        FilledTonalButton(
            modifier = Modifier.weight(1f),
            onClick = onVideoCallClick,
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
            )
        }
    }
}