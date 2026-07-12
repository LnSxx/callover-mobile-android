package com.callover.android.features.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.callover.android.R
import com.callover.android.features.change_password.ChangePasswordScreen
import com.callover.android.features.contact_details.ContactDetailsScreen
import com.callover.android.features.contacts.ContactsScreen
import com.callover.android.features.create_contact.CreateContactScreen
import com.callover.android.features.delete_account.DeleteAccountScreen
import com.callover.android.features.edit_contact_name.EditContactNameScreen
import com.callover.android.features.edit_contact_note.EditContactNoteScreen
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
                ContactsScreen(
                    onCreateContactClick = {
                        navController.navigate(MainRoutes.CONTACTS_CREATE_CONTACT)
                    },
                    onContactClick = { contactId ->
                        navController.navigate(MainRoutes.contactDetail(contactId))
                    },
                )
            }
            composable(MainRoutes.CONTACTS_CREATE_CONTACT) {
                CreateContactScreen(
                    onContactCreated = { contactId ->
                        navController.navigate(MainRoutes.contactDetail(contactId)) {
                            popUpTo(MainRoutes.CONTACTS_CREATE_CONTACT) {
                                inclusive = true
                            }
                        }
                    },
                )
            }
            composable(
                route = MainRoutes.CONTACTS_CONTACT_DETAILS,
            ) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getString("contactId")
                    ?: return@composable

                ContactDetailsScreen(
                    contactId = contactId,
                    onEditNameClick = {
                        navController.navigate(MainRoutes.editContactName(contactId))
                    },
                    onEditNoteClick = {
                        navController.navigate(MainRoutes.editContactNote(contactId))
                    },
                    onContactDeleted = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = MainRoutes.CONTACTS_EDIT_CONTACT_NAME,
            ) { backStackEntry ->
                EditContactNameScreen(
                    onContactNameEdited = {
                        navController.popBackStack()
                    },
                )
            }
            composable(
                route = MainRoutes.CONTACTS_EDIT_CONTACT_NOTE,
            ) { backStackEntry ->
                EditContactNoteScreen(
                    onContactNoteEdited = {
                        navController.popBackStack()
                    },
                )
            }

            composable(MainRoutes.NOTIFICATIONS) {
                NotificationsScreen()
            }

            composable(MainRoutes.SETTINGS) {
                SettingsScreen(
                    onChangePasswordClick = {
                        navController.navigate(MainRoutes.SETTINGS_CHANGE_PASSWORD)
                    },
                    onDeleteAccountClick = {
                        navController.navigate(MainRoutes.SETTINGS_DELETE_ACCOUNT)
                    }
                )
            }
            composable(MainRoutes.SETTINGS_CHANGE_PASSWORD) {
                ChangePasswordScreen()
            }
            composable(MainRoutes.SETTINGS_DELETE_ACCOUNT) {
                DeleteAccountScreen()
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