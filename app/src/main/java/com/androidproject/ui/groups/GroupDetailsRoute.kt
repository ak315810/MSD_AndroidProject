package com.androidproject.ui.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroupDetailsRoute(
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onAddExpense: () -> Unit
) {
    val appContext = LocalContext.current.applicationContext
    val viewModel: GroupDetailsViewModel = viewModel(
        factory = remember(appContext) { GroupDetailsViewModel.factory(appContext) }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    GroupDetailsScreen(
        state = state,
        onBack = onBack,
        onOpenSettings = onOpenSettings,
        onAddExpense = onAddExpense
    )
}