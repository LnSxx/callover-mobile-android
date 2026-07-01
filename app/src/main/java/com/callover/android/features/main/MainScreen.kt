package com.callover.android.features.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.callover.android.R
import com.callover.android.features.contacts.ContactsScreen
import com.callover.android.features.home.HomeScreen
import com.callover.android.features.notifications.NotificationsScreen
import com.callover.android.features.settings.SettingsScreen
import com.callover.android.ui.components.CalloverBottomNavigationBar
import com.callover.android.ui.components.CalloverTopBar

@Composable
fun MainScreen() {
    var selectedDestination by rememberSaveable {
        mutableStateOf(MainScreenDestination.Home)
    }

    Scaffold(
        topBar = {
            CalloverTopBar(titleRes = R.string.app_name)
        },
        bottomBar = {
            CalloverBottomNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationClick = { selectedDestination = it },
            )
        },
    ) { innerPadding ->
        MainScreenContent(
            selectedDestination = selectedDestination,
            innerPadding = innerPadding,
        )
    }
}

@Composable
private fun MainScreenContent(
    selectedDestination: MainScreenDestination,
    innerPadding: PaddingValues,
) {
    when (selectedDestination) {
        MainScreenDestination.Home -> {
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }

        MainScreenDestination.Contacts -> {
            ContactsScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }

        MainScreenDestination.Notifications -> {
            NotificationsScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }

        MainScreenDestination.Settings -> {
            SettingsScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}