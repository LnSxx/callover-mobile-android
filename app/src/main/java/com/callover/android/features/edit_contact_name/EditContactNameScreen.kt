package com.callover.android.features.edit_contact_name

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R

@Composable
fun EditContactNameScreen(
    onContactNameEdited: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditContactNameViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditContactNameEvent.ContactNameEdited -> {
                    onContactNameEdited(event.contactId)
                }
            }
        }
    }

    EditContactNameScreenContent(
        modifier = modifier,
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onSaveClick = viewModel::editName,
    )
}

@Composable
private fun EditContactNameScreenContent(
    uiState: EditContactNameUiState,
    onNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            text = stringResource(R.string.edit_contact_name_title),
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
            text = stringResource(R.string.contacts_new_name_label),
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = uiState.newName,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.name_label)) },
            singleLine = true,
            enabled = !uiState.isLoading && uiState.contact != null,
            isError = uiState.newNameError != null,
            supportingText = {
                uiState.newNameError?.let { error ->
                    Text(error)
                }
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClick,
            enabled = uiState.canSubmit,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(R.string.save_button))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}