package com.callover.android.features.contact_details

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.core.domain.models.Contact
import com.callover.android.features.contact_details.components.ContactActions
import com.callover.android.features.contact_details.components.ContactInfoCard
import com.callover.android.features.create_contact.CreateContactEvent
import com.callover.android.ui.components.LabeledDivider
import com.callover.android.ui.theme.CalloverMobileTheme
import kotlinx.coroutines.launch

@Composable
fun ContactDetailsScreen(
    contactId: String,
    onEditNameClick: () -> Unit,
    onEditNoteClick: () -> Unit,
    onContactDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()

    LaunchedEffect(contactId) {
        viewModel.setContactId(contactId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ContactDetailsEvent.ContactDeleted -> {
                    onContactDeleted()
                }
            }
        }
    }

    ContactDetailsScreenContent(
        modifier = modifier,
        onEditNameClick = onEditNameClick,
        onEditNoteClick = onEditNoteClick,
        uiState = uiState,
        actionState = actionState,
        onToggleIsFavouriteClick = viewModel::toggleFavourite,
        onToggleIsMutedClick = viewModel::toggleMuted,
        onToggleIsBlockedClick = viewModel::toggleBlocked,
        onDeleteClick = viewModel::deleteContact,
    )
}

@Composable
fun ContactDetailsScreenContent(
    uiState: ContactDetailsUiState,
    actionState: ContactDetailsActionState,
    onEditNameClick: () -> Unit,
    onEditNoteClick: () -> Unit,
    onToggleIsFavouriteClick: () -> Unit,
    onToggleIsMutedClick: () -> Unit,
    onToggleIsBlockedClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.contact_details_title),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineLarge,
            )

            Column {
                IconButton(
                    onClick = {
                        isMenuExpanded = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.contact_options),
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = {
                        isMenuExpanded = false
                    },
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.copy_account_id))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            scope.launch {
                                clipboardManager.setClipEntry(
                                    ClipEntry(
                                        ClipData.newPlainText("Account ID", uiState.contact?.contactUserId.orEmpty())
                                    )
                                )
                            }
                        },
                    )

                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.edit_name))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onEditNameClick()
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.contact == null -> {
                Text(
                    text = stringResource(R.string.contact_not_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> {
                ContactDetailsContent(
                    contact = uiState.contact,
                    onEditNoteClick = onEditNoteClick,
                    onToggleIsFavouriteClick = onToggleIsFavouriteClick,
                    onToggleIsMutedClick = onToggleIsMutedClick,
                    onToggleIsBlockedClick = onToggleIsBlockedClick,
                    actionState = actionState,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}

@Composable
private fun ContactDetailsContent(
    contact: Contact,
    onEditNoteClick: () -> Unit,
    onToggleIsFavouriteClick: () -> Unit,
    onToggleIsMutedClick: () -> Unit,
    onToggleIsBlockedClick: () -> Unit,
    onDeleteClick: () -> Unit,
    actionState: ContactDetailsActionState,
) {
    val hasNote = !contact.note.isNullOrBlank()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ContactInfoCard(
            name = contact.alias,
            userId = contact.contactUserId,
            isFavourite = contact.isFavourite,
            isBlocked = contact.isBlocked,
            isMuted = contact.isMuted,
            isOnline = false,
        )

        Spacer(modifier = Modifier.height(24.dp))

        LabeledDivider(
            text = stringResource(R.string.note)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (hasNote) {
            Text(
                text = contact.note.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        TextButton (
            modifier = Modifier.fillMaxWidth(),
            onClick = onEditNoteClick,
        ) {
            Text(
                text = stringResource(
                    if (hasNote) {
                        R.string.edit_note_button
                    } else {
                        R.string.add_note_button
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LabeledDivider(
            text = stringResource(R.string.actions)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (actionState.actionError != null) {
            Text(
                text = actionState.actionError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        ContactActions(
            isFavourite = contact.isFavourite,
            isMuted = contact.isMuted,
            isBlocked = contact.isBlocked,
            onToggleIsFavouriteClick = onToggleIsFavouriteClick,
            onToggleIsMutedClick = onToggleIsMutedClick,
            onToggleIsBlockedClick = onToggleIsBlockedClick,
            onDeleteClick = onDeleteClick,
            isLoading = actionState.isActionLoading,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun ContactDetailsScreenPreview() {
    CalloverMobileTheme {
        ContactDetailsScreenContent(
            uiState = ContactDetailsUiState(
                isLoading = false,
                contact = Contact(
                    id = "contact-id",
                    ownerId = "owner-id",
                    contactUserId = "user-id",
                    alias = "Catherine II the Great",
                    note = null,
                    isFavourite = true,
                    isBlocked = false,
                    isMuted = false,
                    createdAt = "2026-01-01T00:00:00.000Z",
                    updatedAt = "2026-01-01T00:00:00.000Z",
                ),
            ),
            actionState = ContactDetailsActionState(
                isActionLoading = false,
                actionError = "Couldn't save. Please try again"
            ),
            onEditNameClick = {},
            onEditNoteClick = {},
            onToggleIsBlockedClick = {},
            onToggleIsMutedClick = {},
            onToggleIsFavouriteClick = {},
            onDeleteClick = {},
        )
    }
}