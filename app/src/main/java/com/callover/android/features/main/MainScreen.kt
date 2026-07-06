package com.callover.android.features.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.R
import com.callover.android.features.contacts.ContactsScreen
import com.callover.android.features.home.HomeScreen
import com.callover.android.features.notifications.NotificationsScreen
import com.callover.android.features.settings.SettingsScreen
import com.callover.android.ui.components.CalloverBottomNavigationBar
import com.callover.android.ui.components.CalloverTopBar

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val unreadNotificationsCount by viewModel
        .unreadNotificationsCount
        .collectAsStateWithLifecycle()

    var selectedDestination by rememberSaveable {
        mutableStateOf(MainScreenDestination.Home)
    }

    Scaffold(
        topBar = {
            CalloverTopBar(titleRes = R.string.app_name)
        },
        bottomBar = {
            CalloverBottomNavigationBar(
                unreadNotificationsCount = unreadNotificationsCount,
                selectedDestination = selectedDestination,
                onDestinationClick = { selectedDestination = it },
            )
        },
        floatingActionButton = {
            if (selectedDestination == MainScreenDestination.Contacts) {
                FloatingActionButton(
                    onClick = {
                        // TODO: navigate to create contact screen
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.contacts_create_contact),
                    )
                }
            }
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