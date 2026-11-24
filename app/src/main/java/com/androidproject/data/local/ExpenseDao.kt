package com.androidproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // 1. Get all expenses for a group (updates automatically when data changes)
    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getExpenses(groupId: String): Flow<List<ExpenseEntity>>

    // One-off read, used for the duplicate check before saving
    @Query("SELECT * FROM expenses WHERE groupId = :groupId")
    suspend fun getExpensesOnce(groupId: String): List<ExpenseEntity>

    // 2. Save an expense locally
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // 3. Update status (e.g., from PENDING to SYNCED)
    @Query("UPDATE expenses SET syncStatus = :newStatus WHERE id = :expenseId")
    suspend fun updateSyncStatus(expenseId: String, newStatus: String)

    // 4. Outbox: add a pending expense ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutbox(outbox: OutboxEntity)

    // 5. Outbox: get all pending expenses
    @Query("SELECT * FROM outbox ORDER BY timestamp ASC")
    suspend fun getPendingOutbox(): List<OutboxEntity>

    // 6. Outbox: remove an expense once synced
    @Query("DELETE FROM outbox WHERE expenseId = :expenseId")
    suspend fun deleteFromOutbox(expenseId: String)

    // Expense + outbox row in one transaction, so we never end up with a PENDING expense
    // that is missing from the outbox (it would then never be synced)
    @Transaction
    suspend fun insertExpenseWithOutbox(expense: ExpenseEntity, outbox: OutboxEntity) {
        insertExpense(expense)
        insertOutbox(outbox)
    }
}