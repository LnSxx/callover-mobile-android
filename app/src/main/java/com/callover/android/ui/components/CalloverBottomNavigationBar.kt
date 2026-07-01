package com.callover.android.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.callover.android.features.main.MainScreenDestination

@Composable
fun CalloverBottomNavigationBar(
    selectedDestination: MainScreenDestination,
    onDestinationClick: (MainScreenDestination) -> Unit,
) {
    NavigationBar {
        MainScreenDestination.entries.forEach { destination ->
            val selected = destination == selectedDestination

            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = stringResource(destination.titleRes),
                    )
                },
                label = {
                    Text(stringResource(destination.titleRes))
                },
            )
        }
    }
}