
package com.example.nextcloud_passwords_wearos.data.remote

data class LoginRequest(
    val serverUrl: String,
    val username: String,
    val pass: String
)
