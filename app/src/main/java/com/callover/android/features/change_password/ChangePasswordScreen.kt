package com.callover.android.features.change_password

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.ui.theme.CalloverMobileTheme

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChangePasswordScreenContent(
        modifier = modifier,
        uiState = uiState,
        onChangeClick = { currentPassword, newPassword ->
            viewModel.changePassword(currentPassword, newPassword)
        },
        onInputChange = {
            viewModel.clearErrors()
        },
    )
}

@Composable
fun ChangePasswordScreenContent(
    uiState: ChangePasswordUiState,
    onChangeClick: (password: String, newPassword: String) -> Unit,
    onInputChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }

    val canSubmit = currentPassword.isNotBlank() &&
            newPassword.isNotBlank() &&
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
            text = stringResource(R.string.change_password_title),
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.change_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
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

        OutlinedTextField(
            value = currentPassword,
            onValueChange = {
                currentPassword = it
                onInputChange()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.current_password_label)) },
            singleLine = true,
            enabled = !uiState.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                onInputChange()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_password_label)) },
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.newPasswordError != null,
            supportingText = {
                uiState.newPasswordError?.let { error ->
                    Text(error)
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
            ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onChangeClick(currentPassword, newPassword)
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
                Text(stringResource(R.string.change_password_button_label))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(
    name = "Change password screen",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun CreateContactScreenPreview() {
    CalloverMobileTheme {
        ChangePasswordScreenContent(
            uiState = ChangePasswordUiState(),
            onChangeClick = { _, _ -> },
            onInputChange = {},
        )
    }
}