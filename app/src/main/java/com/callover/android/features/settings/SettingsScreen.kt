package com.callover.android.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.callover.android.features.settings.components.ProfileInfo
import com.callover.android.features.settings.components.SettingsActions
import com.callover.android.ui.components.ApplicationInfo
import com.callover.android.ui.components.LabeledDivider

@Composable
fun SettingsScreen(
    onChangePasswordClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onChangePasswordClick = onChangePasswordClick,
        onLogoutClick = viewModel::logout,
        onDeleteAccountClick = onDeleteAccountClick,
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
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

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.userId.isBlank() -> {
                Text(
                    text = "User information is unavailable.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> {
                ProfileInfo(
                    username = uiState.username,
                    userId = uiState.userId,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LabeledDivider(
            text = stringResource(R.string.account)
        )

        Spacer(modifier = Modifier.height(32.dp))

        SettingsActions(
            onChangePasswordClick = onChangePasswordClick,
            onLogoutClick = onLogoutClick,
            onDeleteAccountClick = onDeleteAccountClick,
        )

        Spacer(modifier = Modifier.height(32.dp))

        LabeledDivider(
            text = stringResource(R.string.application)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ApplicationInfo()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(
    name = "Settings Screen Preview",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun SettingsScreenPreview() {
    SettingsScreenContent(
        modifier = Modifier.fillMaxSize(),
        uiState = SettingsUiState(
            username = "Catherine II the Great",
            userId = "dpad2-f23-ff2f2f-24ffi-fbdfb",
            isLoading = false,
        ),
        onChangePasswordClick = {},
        onLogoutClick = {},
        onDeleteAccountClick = {},
    )
}