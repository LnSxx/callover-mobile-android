package com.callover.android.features.login

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.ui.components.CalloverTopBar
import com.callover.android.ui.components.PasswordTextField
import com.callover.android.ui.theme.CalloverMobileTheme

@Composable
fun LoginScreen(
    onOpenRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreenContent(
        uiState = uiState,
        onLoginClick = { username, password ->
            viewModel.login(username, password)
        },
        onInputChange = {
            viewModel.clearErrors()
        },
        onOpenRegisterClick = onOpenRegisterClick,
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onLoginClick: (username: String, password: String) -> Unit,
    onInputChange: () -> Unit,
    onOpenRegisterClick: () -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val canSubmit = username.isNotBlank() &&
            password.isNotBlank() &&
            !uiState.isLoading

    Scaffold(
        topBar = {
            CalloverTopBar(titleRes = R.string.app_name)
        }
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
                text = stringResource(R.string.sign_in_title),
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.sign_in_subtitle),
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

            PasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    onInputChange()
                },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.password_label),
                enabled = !uiState.isLoading,
                isError = uiState.passwordError != null,
                supportingText = {
                    uiState.passwordError?.let { error ->
                        Text(error)
                    }
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onLoginClick(username.trim(), password)
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
                    Text(stringResource(R.string.sign_in_button))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.dont_have_an_account),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = onOpenRegisterClick,
                    enabled = !uiState.isLoading,
                ) {
                    Text(
                        text = stringResource(R.string.create_one),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Sign in screen",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun LoginScreenPreview() {
    CalloverMobileTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onLoginClick = { _, _ -> },
            onInputChange = {},
            onOpenRegisterClick = {},
        )
    }
}

@Preview(
    name = "Sign in screen loading",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun LoginScreenLoadingPreview() {
    CalloverMobileTheme {
        LoginScreenContent(
            uiState = LoginUiState(isLoading = true),
            onLoginClick = { _, _ -> },
            onInputChange = {},
            onOpenRegisterClick = {},
        )
    }
}

@Preview(
    name = "Sign in screen errors",
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
)
@Composable
private fun LoginScreenErrorsPreview() {
    CalloverMobileTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                usernameError = "Username is required",
                passwordError = "Password is required",
            ),
            onLoginClick = { _, _ -> },
            onInputChange = {},
            onOpenRegisterClick = {},
        )
    }
}