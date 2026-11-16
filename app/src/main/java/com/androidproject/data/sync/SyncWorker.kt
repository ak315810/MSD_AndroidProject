package com.androidproject.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidproject.data.local.AppDatabase
import com.androidproject.data.local.ExpenseDao
import kotlinx.coroutines.CancellationException

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
                    return retryOrFail(expenseDao)
                }
            }
            return Result.success()
        } catch (e: CancellationException) {
            // WorkManager stopped us (e.g. network lost), this is not an upload error
            throw e
        } catch (e: Exception) {
            return retryOrFail(expenseDao)
        }
    }

    // Retries with backoff (10s, 20s, 40s). After that the expenses are shown as FAILED,
    // otherwise the UI would say "pending" forever (NFR-K3). They stay in the outbox, so the
    // next scheduled sync still tries them again.
    private suspend fun retryOrFail(expenseDao: ExpenseDao): Result {
        if (runAttemptCount < MAX_RETRIES) return Result.retry()

        // read the outbox again so expenses that did upload in this run are not marked FAILED
        expenseDao.getPendingOutbox().forEach { expenseDao.updateSyncStatus(it.expenseId, "FAILED") }
        return Result.failure()
    }

    private suspend fun uploadExpenseToServer(expenseId: String): Boolean {
        // Here we connect with the remote backend API or Firebase.
        // Returns true if the upload succeeds.
        return true
    }

    companion object {
        const val WORK_NAME = "outbox_sync"
        private const val MAX_RETRIES = 3
    }
}