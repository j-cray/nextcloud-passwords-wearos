
package com.example.nextcloud_passwords_wearos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nextcloud_passwords_wearos.data.local.TokenManager
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.example.nextcloud_passwords_wearos.workers.SyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class SettingsViewModel(
    private val tokenManager: TokenManager,
    private val repository: PasswordRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _theme = MutableStateFlow(tokenManager.getTheme())
    val theme = _theme.asStateFlow()
    
    // Sync mode: "manual", "on_open", "periodic"
    private val _syncMode = MutableStateFlow(tokenManager.getSyncMode())
    val syncMode = _syncMode.asStateFlow()

    fun setTheme(newTheme: String) {
        tokenManager.saveTheme(newTheme)
        _theme.value = newTheme
    }
    
    fun setSyncMode(mode: String) {
        tokenManager.saveSyncMode(mode)
        _syncMode.value = mode
        
        if (mode == "periodic") {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .build()
            workManager.enqueueUniquePeriodicWork(
                "sync_passwords",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        } else {
            workManager.cancelUniqueWork("sync_passwords")
        }
    }
    
    fun logout() {
        repository.logout()
    }
}
