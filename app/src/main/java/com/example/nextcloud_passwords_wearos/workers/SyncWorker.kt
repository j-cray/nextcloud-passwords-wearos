
package com.example.nextcloud_passwords_wearos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: PasswordRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            if (repository.isLoggedIn()) {
                repository.getPasswords() // This fetches and caches
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
