package com.androidproject.ui.groups

enum class SyncStatus { SYNCED, PENDING, FAILED }

data class ExpenseUi(
    val id: String,
    val title: String,
    val amount: Double,
    val paidBy: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class GroupDetailsUiState(
    val groupName: String,
    val description: String,
    val expenses: List<ExpenseUi> = emptyList(),
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val pendingCount: Int = 0
) {
    val isEmpty: Boolean get() = !isLoading && expenses.isEmpty()
}
