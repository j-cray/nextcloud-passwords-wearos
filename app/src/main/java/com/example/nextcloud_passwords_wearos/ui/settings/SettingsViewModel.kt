
package com.example.nextcloud_passwords_wearos.ui.settings

import androidx.lifecycle.ViewModel
import com.example.nextcloud_passwords_wearos.data.local.TokenManager
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val tokenManager: TokenManager,
    private val repository: PasswordRepository
) : ViewModel() {

    private val _theme = MutableStateFlow(tokenManager.getTheme())
    val theme = _theme.asStateFlow()

    fun setTheme(newTheme: String) {
        tokenManager.saveTheme(newTheme)
        _theme.value = newTheme
    }
    
    fun logout() {
        repository.logout()
    }
}
