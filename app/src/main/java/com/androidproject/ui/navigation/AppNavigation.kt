
package com.androidproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.androidproject.ui.groups.Group
import com.androidproject.ui.groups.HomeScreen
import com.androidproject.ui.groups.MyGroupsScreen
import com.androidproject.ui.groups.AddGroupScreen
import com.androidproject.ui.groups.GROUP_ID_ARG
import com.androidproject.ui.groups.GroupDetailsRoute
import com.androidproject.ui.groups.GroupSettingsRoute

import com.androidproject.ui.notifications.NotificationsScreen
import com.androidproject.ui.notifications.NotificationSettingsScreen
import com.androidproject.ui.notifications.RepaymentReminderScreen

import com.androidproject.ui.theme.AndroidProjectTheme

// Group routes (S9 and S11)
private const val GROUP_DETAILS_ROUTE = "group/{$GROUP_ID_ARG}"
private const val GROUP_SETTINGS_ROUTE = "group/{$GROUP_ID_ARG}/settings"

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // Temporary group data
    val groups = remember {
        mutableStateListOf(
            Group(1, "Erasmus Flat", 4),
            Group(2, "Copenhagen Trip", 6),
            Group(3, "Birthday Party", 8)
        )
    }

    // Observe the current destination
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    val groupIdArguments = listOf(
        navArgument(GROUP_ID_ARG) {
            type = NavType.StringType
        }
    )

    // Navigation between main tabs
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

            // Hide bottom navigation on secondary screens
            if (currentRoute !in listOf(
                    "add_group",
                    "notification_settings",
                    "repayment_reminder"
                )
            ) {
                BottomNavigationBar(
                    currentRoute = currentRoute,

                    onHomeClick = {
                        navigateToTab("home")
                    },

                    onGroupsClick = {
                        navigateToTab("groups")
                    },

                    onNotificationsClick = {
                        navigateToTab("notifications")
                    },

                    onProfileClick = {
                        // TODO: Connect S7 when Diego's
                        // authentication branch is merged.
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            // S4 - Home
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

            // S5 - My Groups
            composable("groups") {
                MyGroupsScreen(
                    groups = groups,

                    onGroupClick = { groupId ->
                        navController.navigate("group/$groupId")
                    },

                    onAddGroupClick = {
                        navController.navigate("add_group")
                    }
                )
            }

            // S8 - Add Group
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

            // S9 - Group Details
            composable(
                route = GROUP_DETAILS_ROUTE,
                arguments = groupIdArguments
            ) { entry ->

                val groupId =
                    entry.arguments?.getString(GROUP_ID_ARG).orEmpty()

                GroupDetailsRoute(
                    onBack = {
                        navController.popBackStack()
                    },

                    onOpenSettings = {
                        navController.navigate(
                            "group/$groupId/settings"
                        )
                    },

                    onAddExpense = {
                        // TODO: Connect S10
                    }
                )
            }

            // S11 - Group Settings
            composable(
                route = GROUP_SETTINGS_ROUTE,
                arguments = groupIdArguments
            ) { entry ->

                val groupId =
                    entry.arguments?.getString(GROUP_ID_ARG).orEmpty()

                GroupSettingsRoute(
                    onBack = {
                        navController.popBackStack()
                    },

                    onGroupDeleted = {
                        groups.removeAll {
                            it.id.toString() == groupId
                        }

                        navController.popBackStack(
                            "groups",
                            inclusive = false
                        )
                    }
                )
            }

            // S6 - Notifications
            composable("notifications") {
                NotificationsScreen(
                    onSettingsClick = {
                        navController.navigate(
                            "notification_settings"
                        )
                    },

                    onSendReminderClick = {
                        navController.navigate(
                            "repayment_reminder"
                        )
                    }
                )
            }

            // S13 - Notification Settings
            composable("notification_settings") {
                NotificationSettingsScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // S12 - Send Repayment Reminder
            composable("repayment_reminder") {
                RepaymentReminderScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },

                    onReminderSent = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppNavigationPreview() {
    AndroidProjectTheme {
        AppNavigation()
    }
}
