
package com.example.nextcloud_passwords_wearos.ui.login

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class ShowQr(val qrContent: String) : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
