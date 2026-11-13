package com.androidproject.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidproject.data.local.AppDatabase

/**
 * Background worker handling resilient sync (NFR-K2).
 * Automatically triggered when internet is restored.
 * Applies exponential backoff (10s, 20s, 40s) on network failure.
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val expenseDao = database.expenseDao()

        // 1. Fetch pending tasks from the Outbox
        val pendingTasks = expenseDao.getPendingOutbox()

        if (pendingTasks.isEmpty()) {
            return Result.success()
        }

        try {
            for (task in pendingTasks) {
                // 2. Upload to remote backend (or mock service)
                val isSuccess = uploadExpenseToServer(task.expenseId)

                if (isSuccess) {
                    // 3. Mark as SYNCED in local database (NFR-K3)
                    expenseDao.updateSyncStatus(task.expenseId, "SYNCED")
                    // 4. Remove from outbox
                    expenseDao.deleteFromOutbox(task.expenseId)
                } else {
                    // Network or server error -> trigger exponential backoff retry
                    return Result.retry()
                }
            }
            return Result.success()
        } catch (e: Exception) {
            // Any network exception triggers retry
            return Result.retry()
        }
    }

    private suspend fun uploadExpenseToServer(expenseId: String): Boolean {
        // Here we connect with the remote backend API or Firebase.
        // Returns true if the upload succeeds.
        return true
    }
}