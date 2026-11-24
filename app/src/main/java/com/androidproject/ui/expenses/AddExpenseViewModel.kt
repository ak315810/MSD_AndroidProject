package com.androidproject.ui.expenses

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.androidproject.data.AuthRepository
import com.androidproject.data.ExpenseRepository
import com.androidproject.data.local.AppDatabase
import com.androidproject.data.local.ExpenseEntity
import com.androidproject.ui.groups.GROUP_ID_ARG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class DuplicateWarning(
    val newExpense: ExpenseEntity,
    val existingExpense: ExpenseEntity
)

data class AddExpenseUiState(
    val isSaving: Boolean = false,
    val duplicateWarning: DuplicateWarning? = null,
    val isSaved: Boolean = false
)

class AddExpenseViewModel(
    savedStateHandle: SavedStateHandle,
    private val currentUserId: String,
    private val findDuplicate: suspend (ExpenseEntity) -> ExpenseEntity?,
    private val saveExpense: suspend (ExpenseEntity) -> Unit,
    private val now: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle.get<String>(GROUP_ID_ARG))

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun save(description: String, amount: Double) {
        if (_uiState.value.isSaving) return
        val expense = ExpenseEntity(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            title = description.trim(),
            amount = amount,
            paidByUserId = currentUserId,
            timestamp = now()
        )
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val duplicate = findDuplicate(expense)
            if (duplicate != null) {
                // Don't save yet, the dialog asks the user first
                _uiState.update {
                    it.copy(isSaving = false, duplicateWarning = DuplicateWarning(expense, duplicate))
                }
            } else {
                store(expense)
            }
        }
    }

    // "Save anyway" in the dialog
    fun confirmDuplicate() {
        val warning = _uiState.value.duplicateWarning ?: return
        _uiState.update { it.copy(isSaving = true, duplicateWarning = null) }
        viewModelScope.launch { store(warning.newExpense) }
    }

    // "Cancel" keeps the form open so the user can change it
    fun dismissDuplicate() {
        _uiState.update { it.copy(duplicateWarning = null) }
    }

    private suspend fun store(expense: ExpenseEntity) {
        saveExpense(expense)
        _uiState.update { it.copy(isSaving = false, isSaved = true) }
    }

    companion object {
        fun factory(appContext: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = ExpenseRepository(
                    AppDatabase.getDatabase(appContext).expenseDao(),
                    appContext
                )
                AddExpenseViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    // The screen is only reachable after login, so the user is never null here
                    currentUserId = AuthRepository.getInstance(appContext).currentUser.value?.id.orEmpty(),
                    findDuplicate = repository::findDuplicateOf,
                    saveExpense = repository::addExpense
                )
            }
        }
    }
}
