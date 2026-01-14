
package com.example.nextcloud_passwords_wearos.ui.detail

import androidx.lifecycle.ViewModel
import com.example.nextcloud_passwords_wearos.data.model.Password
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PasswordDetailViewModel(
    private val repository: PasswordRepository
) : ViewModel() {

    private val _password = MutableStateFlow<Password?>(null)
    val password = _password.asStateFlow()

    fun loadPassword(id: String) {
        _password.value = repository.getPassword(id)
    }
}
