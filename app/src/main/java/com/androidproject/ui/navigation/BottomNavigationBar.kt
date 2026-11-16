
package com.androidproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
    // Group details/settings (S9, S11) are part of the Groups tab, so keep that tab highlighted there
    val inGroupsTab = currentRoute == "groups" || currentRoute?.startsWith("group/") == true

    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = inGroupsTab,
            onClick = onGroupsClick,
            icon = { Icon(Icons.Filled.Groups, contentDescription = null) },
            label = { Text("Groups") }
        )

        NavigationBarItem(
            selected = currentRoute == "notifications",
            onClick = onNotificationsClick,
            enabled = false,
            icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
            label = { Text("Notifications") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Profile") }
        )
    }
}
