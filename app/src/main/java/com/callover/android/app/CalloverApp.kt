package com.callover.android.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.callover.android.features.auth.LoginScreen
import com.callover.android.features.auth.RegisterScreen
import com.callover.android.features.main.MainScreen

private enum class AuthScreen {
    Login,
    Register,
    Main,
}

@Composable
fun CalloverApp() {
    var currentScreen by rememberSaveable {
        mutableStateOf(AuthScreen.Main)
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

        AuthScreen.Main -> {
            MainScreen()
        }
    }
}