
package com.androidproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.androidproject.ui.groups.GroupsViewModel
import com.androidproject.ui.groups.HomeScreen
import com.androidproject.ui.groups.MyGroupsScreen
import com.androidproject.ui.groups.AddGroupScreen
import androidx.navigation.NavHostController
import com.androidproject.ui.groups.GROUP_ID_ARG
import com.androidproject.ui.groups.GroupDetailsRoute
import com.androidproject.ui.groups.GroupSettingsRoute
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.navigation
import com.androidproject.data.AuthRepository
import com.androidproject.ui.auth.AuthRoutes
import com.androidproject.ui.auth.LoginRoute
import com.androidproject.ui.auth.ProfileRoute
import com.androidproject.ui.auth.RegisterRoute
import com.androidproject.ui.auth.WelcomeScreen

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

    // Skip the welcome flow when a saved session exists (decided once, at startup)
    val context = LocalContext.current
    val startDestination = remember {
        if (AuthRepository.getInstance(context.applicationContext).isLoggedIn()) {
            "home"
        } else {
            AuthRoutes.GRAPH
        }
    }

    // created here (NavHost level) so every screen shares the same instance
    val groupsViewModel: GroupsViewModel = viewModel()
    val groups by groupsViewModel.groups.collectAsStateWithLifecycle()

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
            // Hidden on secondary screens and the auth flow; currentRoute is null for a moment on start-up
            if (currentRoute != null &&
                currentRoute !in listOf("add_group", "notification_settings", "repayment_reminder") &&
                currentRoute !in AuthRoutes.authFlow
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
                        navigateToTab(AuthRoutes.PROFILE)
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
        ) {

            // S1-S3 as a nested graph, so it can be removed from the back stack as one block
            // after login (back from Home should not return to the login screen)
            navigation(startDestination = AuthRoutes.WELCOME, route = AuthRoutes.GRAPH) {
                composable(AuthRoutes.WELCOME) {
                    WelcomeScreen(
                        onLoginClick = { navController.navigate(AuthRoutes.LOGIN) },
                        onRegisterClick = { navController.navigate(AuthRoutes.REGISTER) }
                    )
                }
                composable(AuthRoutes.LOGIN) {
                    LoginRoute(
                        onBack = { navController.popBackStack() },
                        onLoggedIn = { navController.navigateToHomeClearingAuth() },
                        onRegisterClick = {
                            navController.navigate(AuthRoutes.REGISTER) {
                                popUpTo(AuthRoutes.WELCOME)
                            }
                        }
                    )
                }
                composable(AuthRoutes.REGISTER) {
                    RegisterRoute(
                        onBack = { navController.popBackStack() },
                        onRegistered = { navController.navigateToHomeClearingAuth() },
                        onLoginClick = {
                            navController.navigate(AuthRoutes.LOGIN) {
                                popUpTo(AuthRoutes.WELCOME)
                            }
                        }
                    )
                }
            }

            // S7: profile settings
            composable(AuthRoutes.PROFILE) {
                ProfileRoute(
                    onLoggedOut = {
                        navController.navigate(AuthRoutes.GRAPH) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

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
                        groupsViewModel.addGroup(name, description)
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
                        groupsViewModel.removeGroup(groupId)
                        navController.popBackStack("groups", inclusive = false)
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


private fun NavHostController.navigateToHomeClearingAuth() {
    navigate("home") {
        popUpTo(AuthRoutes.GRAPH) { inclusive = true }
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
