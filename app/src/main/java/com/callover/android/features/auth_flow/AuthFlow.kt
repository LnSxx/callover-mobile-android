package com.callover.android.features.auth_flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.callover.android.features.login.LoginScreen
import com.callover.android.features.register.RegisterScreen

private enum class AuthFlowScreen {
    Login,
    Register,
}

@Composable
fun AuthFlow() {
    var currentScreen by rememberSaveable {
        mutableStateOf(AuthFlowScreen.Login)
    }

    when (currentScreen) {
        AuthFlowScreen.Login -> {
            LoginScreen(
                onOpenRegisterClick = {
                    currentScreen = AuthFlowScreen.Register
                },
            )
        }

        AuthFlowScreen.Register -> {
            RegisterScreen(
                onOpenLoginClick = {
                    currentScreen = AuthFlowScreen.Login
                },
            )
        }
    }
}