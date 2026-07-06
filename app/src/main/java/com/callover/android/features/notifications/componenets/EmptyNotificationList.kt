package com.callover.android.features.notifications.componenets

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.callover.android.R

@Composable
fun EmptyNotificationList() {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.empty_notification_list_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}