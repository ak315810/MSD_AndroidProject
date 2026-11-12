package com.androidproject.ui.groups

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.androidproject.data.ConnectivityObserver
import com.androidproject.data.ExpenseRepository
import com.androidproject.data.NetworkConnectivityObserver
import com.androidproject.data.local.AppDatabase
import com.androidproject.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class GroupDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    getExpenses: (String) -> Flow<List<ExpenseEntity>>,
    connectivity: ConnectivityObserver
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle.get<String>(GROUP_ID_ARG))

    val uiState: StateFlow<GroupDetailsUiState> =
        combine(getExpenses(groupId), connectivity.isOnline) { entities, isOnline ->
            val expenses = entities.map { it.toUi() }
            GroupDetailsUiState(
                // TODO: read name and description from the group repository when it exists
                groupName = "Group $groupId",
                description = "",
                expenses = expenses,
                isLoading = false,
                isOffline = !isOnline,
                pendingCount = expenses.count { it.syncStatus != SyncStatus.SYNCED }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GroupDetailsUiState(groupName = "", description = "", isLoading = true)
        )

    companion object {
        fun factory(appContext: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = ExpenseRepository(
                    AppDatabase.getDatabase(appContext).expenseDao(),
                    appContext
                )
                GroupDetailsViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    getExpenses = repository::getExpenses,
                    connectivity = NetworkConnectivityObserver(appContext)
                )
            }
        }
    }
}

internal fun ExpenseEntity.toUi() = ExpenseUi(
    id = id,
    title = title,
    amount = amount,
    paidBy = paidByUserId,
    syncStatus = syncStatus.toSyncStatus()
)

private fun String.toSyncStatus() = when (this) {
    "PENDING" -> SyncStatus.PENDING
    "FAILED" -> SyncStatus.FAILED
    else -> SyncStatus.SYNCED
}