
package com.androidproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // S4 - Home
        composable("home") {
            HomeScreen(
                onMyGroupsClick = {
                    navController.navigate("groups")
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
                    // TODO: Connect to S9 (Group Details)
                    // when Marianna's screen is ready.
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
                    val newId = (groups.maxOfOrNull { it.id } ?: 0) + 1

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
