
package com.androidproject.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Text("⌂") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "groups",
            onClick = onGroupsClick,
            icon = { Text("☷") },
            label = { Text("Groups") }
        )

        NavigationBarItem(
            selected = currentRoute == "notifications",
            onClick = onNotificationsClick,
            enabled = false,
            icon = { Text("♧") },
            label = { Text("Notifications") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            enabled = false,
            icon = { Text("⚙") },
            label = { Text("Profile") }
        )
    }
}
