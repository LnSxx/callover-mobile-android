package com.callover.android.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.callover.android.features.LoginScreen
import com.callover.android.features.RegisterScreen

private enum class AuthScreen {
    Login,
    Register
}

@Composable
fun CalloverApp() {
    var currentScreen by rememberSaveable {
        mutableStateOf(AuthScreen.Login)
    }

    when (currentScreen) {
        AuthScreen.Login -> {
            LoginScreen(
                onLoginClick = { username, password ->
                    println("Login: $username / $password")
                },
                onOpenRegisterClick = {
                    currentScreen = AuthScreen.Register
                },
            )
        }

        AuthScreen.Register -> {
            RegisterScreen(
                onRegisterClick = { username, password ->
                    println("Register: $username / $password")
                },
                onOpenLoginClick = {
                    currentScreen = AuthScreen.Login
                },
            )
        }
    }
}