package com.callover.android.features.contacts

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.features.contacts.components.ContactsList
import com.callover.android.features.contacts.components.EmptyContactList
import com.callover.android.ui.theme.CalloverMobileTheme

@Composable
fun ContactsScreen(
    onCreateContactClick: () -> Unit,
    onContactClick: (contactId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactsScreenContent(
        onCreateContactClick = onCreateContactClick,
        onContactClick = onContactClick,
        onAudioClick = viewModel::startAudioCall,
        onVideoClick = viewModel::startVideoCall,
        modifier = modifier,
        uiState = uiState,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ContactsScreenContent(
    uiState: ContactsUiState,
    onCreateContactClick: () -> Unit,
    onContactClick: (contactId: String) -> Unit,
    onAudioClick: (peerUserId: String) -> Unit,
    onVideoClick: (peerUserId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateContactClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.contacts_create_contact),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.contacts_title),
                    style = MaterialTheme.typography.headlineLarge,
                )

                if (uiState.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                !uiState.hasLoaded -> Unit

                uiState.contacts.isEmpty() -> EmptyContactList()

                else -> ContactsList(
                    contacts = uiState.contacts,
                    onlineUserIds = uiState.onlineUserIds,
                    onContactClick = onContactClick,
                    onAudioClick = onAudioClick,
                    onVideoClick = onVideoClick,
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Preview(
    name = "Contacts Screen Preview",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun ContactsScreenPreview() {
    CalloverMobileTheme(
        darkTheme = true,
    ) {
        ContactsScreenContent(
            onCreateContactClick = {},
            onContactClick = { _ -> },
            onAudioClick = { _ -> },
            onVideoClick = { _ -> },
            uiState = ContactsUiState(
                contacts = emptyList(),
                isSyncing = false,
                errorMessage = null,
            )
        )
    }
}