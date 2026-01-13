
package com.example.nextcloud_passwords_wearos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
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
) : ViewModel(), MessageClient.OnMessageReceivedListener {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        messageClient.addListener(this)
        viewModelScope.launch {
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

    override fun onCleared() {
        super.onCleared()
        messageClient.removeListener(this)
    }

    fun showQrCode() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = LoginUiState.Loading
            try {
                val localNode = Tasks.await(nodeClient.localNode)
                val nodeId = localNode.id
                // Scheme: nextcloud-passwords://wear-login?nodeId=<nodeId>
                val qrContent = "nextcloud-passwords://wear-login?nodeId=$nodeId"
                _uiState.value = LoginUiState.ShowQr(qrContent)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Failed to generate QR: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/credentials") {
            val data = String(messageEvent.data)
            val parts = data.split("|")
            if (parts.size == 3) {
                val server = parts[0]
                val user = parts[1]
                val pass = parts[2]
                login(server, user, pass)
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
