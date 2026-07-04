package com.callover.android.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.callover.android.features.main.MainScreenDestination

@Composable
fun CalloverBottomNavigationBar(
    unreadNotificationsCount: Int,
    selectedDestination: MainScreenDestination,
    onDestinationClick: (MainScreenDestination) -> Unit,
) {
    NavigationBar {
        MainScreenDestination.entries.forEach { destination ->
            val selected = destination == selectedDestination
            val shouldShowNotificationsBadge =
                destination == MainScreenDestination.Notifications &&
                        unreadNotificationsCount > 0

            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (shouldShowNotificationsBadge) {
                                Badge()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = if (selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = stringResource(destination.titleRes),
                        )
                    }
                },
                label = {
                    Text(stringResource(destination.titleRes))
                },
            )
        }
    }
}