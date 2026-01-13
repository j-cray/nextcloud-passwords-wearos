
package com.example.nextcloud_passwords_wearos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repository: PasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading) // Start with Loading
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Check login status on IO thread to avoid blocking main thread with EncryptedSharedPreferences init
            val loggedIn = withContext(Dispatchers.IO) {
                repository.isLoggedIn()
            }
            if (loggedIn) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Idle
            }
        }
    }

    fun login(serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                // Repository operations should be main-safe, but explicit IO context doesn't hurt
                withContext(Dispatchers.IO) {
                    repository.login(serverUrl, username, password)
                }
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logout()
            _uiState.value = LoginUiState.Idle
        }
    }
}
