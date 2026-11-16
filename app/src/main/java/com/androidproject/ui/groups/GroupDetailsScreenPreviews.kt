package com.androidproject.ui.groups

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.androidproject.ui.theme.AndroidProjectTheme

private val mockExpenses = listOf(
    ExpenseUi("1", "Dinner", 450.0, "Person 1"),
    ExpenseUi("2", "Taxi", 120.5, "Person 2", syncStatus = SyncStatus.PENDING),
    ExpenseUi("3", "Groceries", 89.9, "Person 3", syncStatus = SyncStatus.FAILED)
)

private val base = GroupDetailsUiState(
    groupName = "Trip to Odense",
    description = "Weekend trip expenses"
)

@Composable
private fun PreviewScreen(state: GroupDetailsUiState) {
    AndroidProjectTheme {
        GroupDetailsScreen(state = state, onBack = {}, onOpenSettings = {}, onAddExpense = {})
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun LoadingPreview() = PreviewScreen(base.copy(isLoading = true))

@Preview(showBackground = true, name = "Empty")
@Composable
private fun EmptyPreview() = PreviewScreen(base.copy(isLoading = false))

@Preview(showBackground = true, name = "Content (synced, pending, failed)")
@Composable
private fun ContentPreview() =
    PreviewScreen(base.copy(isLoading = false, expenses = mockExpenses, pendingCount = 2))

@Preview(showBackground = true, name = "Offline")
@Composable
private fun OfflinePreview() = PreviewScreen(
    base.copy(isLoading = false, expenses = mockExpenses, isOffline = true, pendingCount = 2)
)

@Preview(showBackground = true, name = "Offline + empty")
@Composable
private fun OfflineEmptyPreview() = PreviewScreen(base.copy(isLoading = false, isOffline = true))
