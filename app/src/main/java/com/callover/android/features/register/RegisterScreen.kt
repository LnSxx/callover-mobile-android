package com.callover.android.features.register

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.callover.android.ui.components.CalloverTopBar
import com.callover.android.ui.theme.CalloverMobileTheme

@Composable
fun RegisterScreen(
    onOpenLoginClick: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterScreenContent(
        uiState = uiState,
        onRegisterClick = { username, password ->
            viewModel.register(username, password)
        },
        onInputChange = {
            viewModel.clearErrors()
        },
        onOpenLoginClick = onOpenLoginClick,
    )
}

@Composable
private fun RegisterScreenContent(
    uiState: RegisterUiState,
    onRegisterClick: (username: String, password: String) -> Unit,
    onInputChange: () -> Unit,
    onOpenLoginClick: () -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val canSubmit = username.isNotBlank() &&
            password.isNotBlank() &&
            !uiState.isLoading

    Scaffold(
        topBar = {
            CalloverTopBar(titleRes = R.string.app_name)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.register_subtitle),
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
                value = username,
                onValueChange = {
                    username = it
                    onInputChange()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.username_label)) },
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.usernameError != null,
                supportingText = {
                    uiState.usernameError?.let { error ->
                        Text(error)
                    }
                },
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    onInputChange()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.passwordError != null,
                supportingText = {
                    uiState.passwordError?.let { error ->
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
                    onRegisterClick(username.trim(), password)
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
                    Text(stringResource(R.string.register_button))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.already_have_an_account),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = onOpenLoginClick,
                    enabled = !uiState.isLoading,
                ) {
                    Text(
                        text = stringResource(R.string.sign_in),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Register screen",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun RegisterScreenPreview() {
    CalloverMobileTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(),
            onRegisterClick = { _, _ -> },
            onInputChange = {},
            onOpenLoginClick = {},
        )
    }
}

@Preview(
    name = "Register screen loading",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun RegisterScreenLoadingPreview() {
    CalloverMobileTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(isLoading = true),
            onRegisterClick = { _, _ -> },
            onInputChange = {},
            onOpenLoginClick = {},
        )
    }
}

@Preview(
    name = "Register screen errors",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun RegisterScreenErrorsPreview() {
    CalloverMobileTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(
                usernameError = "Username is already taken",
                passwordError = "Password is too short",
            ),
            onRegisterClick = { _, _ -> },
            onInputChange = {},
            onOpenLoginClick = {},
        )
    }
}