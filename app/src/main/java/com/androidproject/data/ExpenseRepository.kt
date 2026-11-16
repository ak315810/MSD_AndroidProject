package com.androidproject.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.androidproject.data.local.ExpenseDao
import com.androidproject.data.local.ExpenseEntity
import com.androidproject.data.local.OutboxEntity
import com.androidproject.data.sync.SyncWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val context: Context
) {

    // Fast local read for the UI (< 500ms, NFR-K1)
    fun getExpenses(groupId: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpenses(groupId)
    }

    // Local-first write (< 100ms, NFR-K2 and NFR-K3)
    suspend fun addExpense(expense: ExpenseEntity) {
        // 1. Save locally with PENDING status
        expenseDao.insertExpense(expense.copy(syncStatus = "PENDING"))

        // 2. Add to outbox queue
        expenseDao.insertOutbox(OutboxEntity(expenseId = expense.id))

        // 3. Schedule background sync with exponential backoff (10s, 20s, 40s)
        scheduleSync()
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            // Exponential backoff: starts at 10 seconds (10s, 20s, 40s...)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}