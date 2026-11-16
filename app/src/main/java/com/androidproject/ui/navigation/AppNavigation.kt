
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

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.androidproject.ui.groups.GROUP_ID_ARG
import com.androidproject.ui.groups.GroupDetailsRoute
import com.androidproject.ui.groups.GroupSettingsRoute

// S9 (group details) and S11 (group settings)
private const val GROUP_DETAILS_ROUTE = "group/{$GROUP_ID_ARG}"
private const val GROUP_SETTINGS_ROUTE = "group/{$GROUP_ID_ARG}/settings"
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

    val isGroupDetailFlow = currentRoute?.startsWith("group/") == true

    val groupIdArguments = listOf(
        navArgument(GROUP_ID_ARG) { type = NavType.StringType }
    )


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
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
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
                    onGroupClick = {  groupId ->
                        navController.navigate("group/$groupId")
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
            // S9: group details
            composable(
                route = GROUP_DETAILS_ROUTE,
                arguments = groupIdArguments
            ) { entry ->
                val groupId = entry.arguments?.getString(GROUP_ID_ARG).orEmpty()

                GroupDetailsRoute(
                    onBack = { navController.popBackStack() },
                    onOpenSettings = { navController.navigate("group/$groupId/settings") },
                    onAddExpense = {
                        // TODO: navigate to S10 (add expense) when it is merged
                    }
                )
            }
            // S11: group settings
            composable(
                route = GROUP_SETTINGS_ROUTE,
                arguments = groupIdArguments
            ) { entry ->
                val groupId = entry.arguments?.getString(GROUP_ID_ARG).orEmpty()

                GroupSettingsRoute(
                    onBack = { navController.popBackStack() },
                    onGroupDeleted = {
                        groups.removeAll { it.id.toString() == groupId }
                        navController.popBackStack("groups", inclusive = false)
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

