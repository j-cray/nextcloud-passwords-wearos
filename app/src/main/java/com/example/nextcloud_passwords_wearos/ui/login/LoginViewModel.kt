
package com.example.nextcloud_passwords_wearos.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repository: PasswordRepository,
    private val messageClient: MessageClient,
    private val nodeClient: NodeClient,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private val _debugStatus = MutableStateFlow("Initializing...")
    val debugStatus = _debugStatus.asStateFlow()

    init {
        viewModelScope.launch {
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
                updateConnectionStatus()
                checkForCredentials()
            }
        }
    }
    
    fun updateConnectionStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nodes = Tasks.await(nodeClient.connectedNodes)
                _debugStatus.value = "Nodes: ${nodes.size} (${nodes.joinToString { it.displayName }})"
            } catch (e: Exception) {
                _debugStatus.value = "Error checking nodes: ${e.message}"
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

    fun checkForCredentials() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = LoginUiState.Loading
            try {
                val dataClient = Wearable.getDataClient(context)
                val dataItems = Tasks.await(dataClient.getDataItems(
                    android.net.Uri.Builder().scheme("wear").path("/wear-credentials").build()
                ))
                
                _debugStatus.value = "Found ${dataItems.count} items"
                
                var found = false
                for (item in dataItems) {
                    val dataMap = DataMapItem.fromDataItem(item).dataMap
                    val server = dataMap.getString("server")
                    val user = dataMap.getString("user")
                    val pass = dataMap.getString("password")
                    
                    if (server != null && user != null && pass != null) {
                        repository.login(server, user, pass)
                        found = true
                        break 
                    }
                }
                
                if (!found) {
                    if (_uiState.value is LoginUiState.Loading) {
                         _uiState.value = LoginUiState.Idle
                    }
                }
            } catch (e: Exception) {
                 _debugStatus.value = "Check failed: ${e.message}"
                 if (_uiState.value is LoginUiState.Loading) {
                     _uiState.value = LoginUiState.Idle
                 }
            }
        }
    }

    fun requestSync() {
        checkForCredentials()
        updateConnectionStatus()
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
