package com.callover.android.features.auth_gate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.core.auth.AuthState
import com.callover.android.features.auth_flow.AuthFlow
import com.callover.android.features.main.MainScreen
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthGate(
    viewModel: AuthGateViewModel = hiltViewModel(),
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.Loading -> {
            LoadingScreen()
        }

        AuthState.Unauthenticated -> {
            AuthFlow()
        }

        is AuthState.Authenticated -> {
            MainScreen()
        }
    }
}