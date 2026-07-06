package com.callover.android.features.contacts

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
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
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactsScreenContent(
        modifier = modifier,
        uiState = uiState,
    )
}

@Composable
private fun ContactsScreenContent(
    uiState: ContactsUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.contacts_title),
                style = MaterialTheme.typography.headlineLarge,
            )

            if (uiState.isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            !uiState.hasLoaded -> Unit

            uiState.contacts.isEmpty() -> EmptyContactList()

            else -> ContactsList(
                contacts = uiState.contacts,
            )
        }

        Spacer(modifier = Modifier.height(88.dp))

//        uiState.errorMessage?.let { errorMessage ->
//            Snackbar(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp),
//            ) {
//                Text(errorMessage)
//            }
//        }
    }
}

@Preview(
    name = "Contacts Screen Preview",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun ContactsScreenPreview() {
    CalloverMobileTheme {
        ContactsScreenContent(
            uiState = ContactsUiState(
                contacts = emptyList(),
                isSyncing = false,
                errorMessage = null,
            )
        )
    }
}