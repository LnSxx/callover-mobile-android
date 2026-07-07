package com.callover.android.features.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.callover.android.R

enum class MainScreenDestination(
    val route: String,
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Home(
        route = MainRoutes.HOME,
        titleRes = R.string.home_title,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    Contacts(
        route = MainRoutes.CONTACTS,
        titleRes = R.string.contacts_title,
        selectedIcon = Icons.Filled.AccountBox,
        unselectedIcon = Icons.Outlined.AccountBox,
    ),
    Notifications(
        route = MainRoutes.NOTIFICATIONS,
        titleRes = R.string.notifications_title,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications,
    ),
    Settings(
        route = MainRoutes.SETTINGS,
        titleRes = R.string.settings_title,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
}