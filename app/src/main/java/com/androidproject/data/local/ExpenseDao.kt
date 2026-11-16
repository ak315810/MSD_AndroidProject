package com.androidproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // 1. Get all expenses for a group (updates automatically when data changes)
    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getExpenses(groupId: String): Flow<List<ExpenseEntity>>

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
}