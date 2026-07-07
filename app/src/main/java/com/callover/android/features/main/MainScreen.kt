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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()

    val unreadNotificationsCount by viewModel
        .unreadNotificationsCount
        .collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val selectedDestination = MainScreenDestination.entries
        .firstOrNull { destination ->
            currentDestination.isCurrentDestination(destination.route)
        }
        ?: MainScreenDestination.Home

    val shouldShowBottomBar = MainScreenDestination.entries.any { destination ->
        currentDestination.isCurrentDestination(destination.route)
    }

    Scaffold(
        topBar = {
            CalloverTopBar(titleRes = R.string.app_name)
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                CalloverBottomNavigationBar(
                    unreadNotificationsCount = unreadNotificationsCount,
                    selectedDestination = selectedDestination,
                    onDestinationClick = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(MainRoutes.HOME) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainRoutes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(MainRoutes.HOME) {
                HomeScreen()
            }

            composable(MainRoutes.CONTACTS) {
                ContactsScreen()
            }

            composable(MainRoutes.NOTIFICATIONS) {
                NotificationsScreen()
            }

            composable(MainRoutes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}

private fun NavDestination?.isCurrentDestination(
    route: String,
): Boolean {
    return this?.hierarchy?.any { destination ->
        destination.route == route
    } == true
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