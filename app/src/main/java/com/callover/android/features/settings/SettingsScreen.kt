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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.features.settings.components.ProfileInfo

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = modifier,
        uiState = uiState,
    )
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
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

            else -> {
                ProfileInfo(
                    username = uiState.username,
                    userId = uiState.userId,
                )
            }
        }
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
    )
}

@Preview(
    name = "Settings Screen Preview Unavailable",
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun SettingsScreenUnavailablePreview() {
    SettingsScreenContent(
        modifier = Modifier.fillMaxSize(),
        uiState = SettingsUiState(
            username = "",
            userId = "",
            isLoading = true,
        ),
    )
}