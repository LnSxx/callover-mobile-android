package com.callover.android.features.create_contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.ui.theme.CalloverMobileTheme

private const val MAX_CONTACT_NAME_LENGTH = 80

@Composable
fun CreateContactScreen(
    modifier: Modifier = Modifier,
    onContactCreated: (contactId: String) -> Unit,
    viewModel: CreateContactViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateContactEvent.ContactCreated -> {
                    onContactCreated(event.contactId)
                }
            }
        }
    }

    CreateContactScreenContent(
        modifier = modifier,
        uiState = uiState,
        onCreateClick = { name, userId ->
            viewModel.createContact(userId, name)
        },
        onInputChange = {
            viewModel.clearErrors()
        },
    )
}

@Composable
fun CreateContactScreenContent(
    uiState: CreateContactUiState,
    onCreateClick: (name: String, userId: String) -> Unit,
    onInputChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }

    val canSubmit = name.isNotBlank() &&
            userId.isNotBlank() &&
            !uiState.isLoading

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

        Text(
            text = stringResource(R.string.create_contact_title),
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.generalError != null) {
            Text(
                text = uiState.generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = stringResource(R.string.name_label_description),
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = name,
            onValueChange = { value ->
                if (value.length <= MAX_CONTACT_NAME_LENGTH) {
                    name = value
                    onInputChange()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.name_label)) },
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.nameError != null,
            supportingText = {
                uiState.nameError?.let { error ->
                    Text(error)
                }
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.user_id_label_description),
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = userId,
            onValueChange = {
                userId = it
                onInputChange()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.user_id_label)) },
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.userIdError != null,
            supportingText = {
                uiState.userIdError?.let { error ->
                    Text(error)
                }
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onCreateClick(name.trim(), userId.trim())
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(R.string.create_button))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(
    name = "Create contact screen",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun CreateContactScreenPreview() {
    CalloverMobileTheme {
        CreateContactScreenContent(
            uiState = CreateContactUiState(),
            onCreateClick = { _, _ -> },
            onInputChange = {},
        )
    }
}

@Preview(
    name = "Create contact screen loading",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun CreateContactScreenLoadingPreview() {
    CalloverMobileTheme {
        CreateContactScreenContent(
            uiState = CreateContactUiState(isLoading = true),
            onCreateClick = { _, _ -> },
            onInputChange = {},
        )
    }
}

@Preview(
    name = "Create contact screen errors",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun CreateContactScreenErrorsPreview() {
    CalloverMobileTheme {
        CreateContactScreenContent(
            uiState = CreateContactUiState(
                nameError = "Name is required",
                userIdError = "Account ID is required",
            ),
            onCreateClick = { _, _ -> },
            onInputChange = {},
        )
    }
}