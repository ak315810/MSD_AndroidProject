
package com.androidproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.androidproject.ui.groups.GroupsViewModel
import com.androidproject.ui.groups.HomeScreen
import com.androidproject.ui.groups.MyGroupsScreen
import com.androidproject.ui.groups.AddGroupScreen

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
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

// S9 (group details) and S11 (group settings)
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

    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

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
            // currentRoute is null for a moment on start-up, so wait for it before showing the bar
            if (currentRoute != null &&
                currentRoute != "add_group" &&
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
                        // TODO: Connect S6 when Ania finishes it
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
                    onCreateGroup = { name, _ ->
                        groupsViewModel.addGroup(name)
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
                        groupsViewModel.removeGroup(groupId)
                        navController.popBackStack("groups", inclusive = false)
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

