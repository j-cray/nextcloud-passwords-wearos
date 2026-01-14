
package com.example.nextcloud_passwords_wearos.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextcloud_passwords_wearos.data.model.Password
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

sealed interface AddPasswordUiState {
    object Idle : AddPasswordUiState
    object Loading : AddPasswordUiState
    object Success : AddPasswordUiState
    data class Error(val message: String) : AddPasswordUiState
}

class AddPasswordViewModel(
    private val repository: PasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddPasswordUiState>(AddPasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun savePassword(label: String, username: String, pass: String, url: String) {
        viewModelScope.launch {
            _uiState.value = AddPasswordUiState.Loading
            try {
                val newPassword = Password(
                    id = UUID.randomUUID().toString(), // Server might ignore this or use it
                    label = label,
                    username = username,
                    password = pass,
                    url = url,
                    cseType = "none"
                )
                withContext(Dispatchers.IO) {
                    repository.createPassword(newPassword)
                }
                _uiState.value = AddPasswordUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddPasswordUiState.Error(e.message ?: "Failed to save")
            }
        }
    }
}
