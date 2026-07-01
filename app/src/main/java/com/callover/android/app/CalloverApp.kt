package com.callover.android.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.callover.android.features.LoginScreen
import com.callover.android.features.RegisterScreen

private enum class AuthScreen {
    Login,
    Register
}

@Composable
fun CalloverApp() {
    val currentScreen = remember { mutableStateOf(AuthScreen.Login) }

    when (currentScreen.value) {
        AuthScreen.Login -> {
            LoginScreen(
                onLoginClick = { username, password ->
                    println("Login: $username / $password")
                },
                onOpenRegisterClick = {
                    currentScreen.value = AuthScreen.Register
                }
            )
        }

        AuthScreen.Register -> {
            RegisterScreen(
                onRegisterClick = { username, password ->
                    println("Register: $username / $password")
                },
                onOpenLoginClick = {
                    currentScreen.value = AuthScreen.Login
                }
            )
        }
    }
}