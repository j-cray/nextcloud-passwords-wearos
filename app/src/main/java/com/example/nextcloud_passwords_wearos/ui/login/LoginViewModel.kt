
package com.example.nextcloud_passwords_wearos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            // TODO: Implement actual login logic with Nextcloud API
            println("Logging in with server: $serverUrl, username: $username")
        }
    }
}
