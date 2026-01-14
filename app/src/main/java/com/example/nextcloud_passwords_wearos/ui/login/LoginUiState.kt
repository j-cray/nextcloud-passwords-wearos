
package com.example.nextcloud_passwords_wearos.ui.login

sealed interface LoginUiState {
    object Idle : LoginUiState
    object EnterServer : LoginUiState
    object Loading : LoginUiState
    data class ShowQr(val qrContent: String) : LoginUiState // For Data Layer sync
    data class ShowFlowQr(val loginUrl: String, val pollToken: String, val pollEndpoint: String) : LoginUiState // For Login Flow v2
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
