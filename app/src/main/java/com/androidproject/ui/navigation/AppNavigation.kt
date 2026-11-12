
package com.androidproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.androidproject.ui.groups.Group
import com.androidproject.ui.groups.HomeScreen
import com.androidproject.ui.groups.MyGroupsScreen
import com.androidproject.ui.groups.AddGroupScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val groups = remember {
        mutableStateListOf(
            Group(1, "Erasmus Flat", 4),
            Group(2, "Copenhagen Trip", 6),
            Group(3, "Birthday Party", 8)
        )
    }

    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    fun navigateToTab(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo("home") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != "add_group") {

                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onHomeClick = {
                        navigateToTab("home")
                    },
                    onGroupsClick = {
                        navigateToTab("groups")
                    },
                    onNotificationsClick = {
                        // TODO: Connect S6 when Ania finishes it
                    },
                    onProfileClick = {
                        // TODO: Connect S7 when Diego finishes it
                    }
                )

            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(
                    onMyGroupsClick = {
                        navigateToTab("groups")
                    },
                    onAddGroupClick = {
                        navController.navigate("add_group")
                    }
                )
            }

            composable("groups") {
                MyGroupsScreen(
                    groups = groups,
                    onGroupClick = { groupId ->
                        // TODO: Navigate to S9
                        // when Marianna's screen is ready.
                    },
                    onAddGroupClick = {
                        navController.navigate("add_group")
                    }
                )
            }

            composable("add_group") {
                AddGroupScreen(
                    onCreateGroup = { name, description ->
                        val newId =
                            (groups.maxOfOrNull { it.id } ?: 0) + 1

                        groups.add(
                            Group(
                                id = newId,
                                name = name,
                                members = 1
                            )
                        )

                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppNavigationPreview() {
    com.androidproject.ui.theme.AndroidProjectTheme {
        AppNavigation()
    }
}

