
package com.example.nextcloud_passwords_wearos.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextcloud_passwords_wearos.data.model.Password
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface PasswordListUiState {
    object Loading : PasswordListUiState
    data class Success(val passwords: List<Password>) : PasswordListUiState
    data class Error(val message: String) : PasswordListUiState
}

class PasswordListViewModel(
    private val repository: PasswordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PasswordListUiState>(PasswordListUiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private var allPasswords: List<Password> = emptyList()

    init {
        loadPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            _uiState.value = PasswordListUiState.Loading
            try {
                val passwords = withContext(Dispatchers.IO) {
                    repository.getPasswords()
                }
                allPasswords = passwords.sortedBy { it.label.lowercase() }
                _uiState.value = PasswordListUiState.Success(allPasswords)
            } catch (e: Exception) {
                _uiState.value = PasswordListUiState.Error(e.message ?: "Failed to load passwords")
            }
        }
    }
    
    fun search(query: String) {
        if (query.isEmpty()) {
            _uiState.value = PasswordListUiState.Success(allPasswords)
            return
        }
        
        val filtered = allPasswords.filter { 
            it.label.contains(query, ignoreCase = true) || 
            it.username.contains(query, ignoreCase = true) 
        }
        _uiState.value = PasswordListUiState.Success(filtered)
    }
}
