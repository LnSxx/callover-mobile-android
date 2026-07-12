package com.callover.android.features.contact_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.callover.android.R

@Composable
fun ContactActions(
    onToggleIsFavouriteClick: () -> Unit,
    onToggleIsMutedClick: () -> Unit,
    onToggleIsBlockedClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isFavourite: Boolean,
    isMuted: Boolean,
    isBlocked: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onToggleIsFavouriteClick,
            enabled = !isLoading
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = if (isFavourite) {
                        Icons.Outlined.Star
                    } else {
                        Icons.Filled.Star
                    },
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(if (isFavourite) {
                        R.string.remove_from_favourites
                    } else {
                        R.string.add_to_favourites
                    }),
                )
            }
        }

        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onToggleIsMutedClick,
            enabled = !isLoading
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = if (isMuted) {
                        Icons.Outlined.Notifications
                    } else {
                        Icons.Outlined.NotificationsOff
                    },
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(if (isMuted) {
                        R.string.unmute
                    } else {
                        R.string.mute
                    }),
                )
            }
        }

        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onToggleIsBlockedClick,
            enabled = !isLoading
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Block,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(if (isBlocked) {
                        R.string.unblock
                    } else {
                        R.string.block
                    }),
                )
            }
        }

        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDeleteClick,
            enabled = !isLoading,
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.delete),
                )
            }
        }
    }
}