
package com.example.nextcloud_passwords_wearos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repository: PasswordRepository,
    private val messageClient: MessageClient,
    private val nodeClient: NodeClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Observe login events from Repository (triggered by WearableCredentialService)
            launch {
                repository.loginEvent.collect {
                    _uiState.value = LoginUiState.Success
                }
            }
            
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

    fun showQrCode() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = LoginUiState.Loading
            try {
                val localNode = Tasks.await(nodeClient.localNode)
                val nodeId = localNode.id
                val qrContent = "nextcloud-passwords://wear-login?nodeId=$nodeId"
                _uiState.value = LoginUiState.ShowQr(qrContent)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Failed to generate QR: ${e.message}")
            }
        }
    }

    fun requestSync() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = LoginUiState.Loading
            try {
                val nodes = Tasks.await(nodeClient.connectedNodes)
                if (nodes.isEmpty()) {
                    _uiState.value = LoginUiState.Error("No phone connected")
                    return@launch
                }
                for (node in nodes) {
                    Tasks.await(messageClient.sendMessage(node.id, "/request-credentials", ByteArray(0)))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Sync failed: ${e.message}")
            }
        }
    }

    fun login(serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
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
