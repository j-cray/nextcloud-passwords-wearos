
package com.example.nextcloud_passwords_wearos.data.repository

import com.example.nextcloud_passwords_wearos.data.remote.NextcloudApi
import com.example.nextcloud_passwords_wearos.data.remote.LoginRequest

class PasswordRepository(
    private val api: NextcloudApi
) {
    suspend fun login(serverUrl: String, username: String, pass: String) {
        // By constructing the LoginRequest here we can modify the Retrofit instance's base URL
        // before making the call. This is a common pattern for multi-tenant apps.
        val request = LoginRequest(serverUrl, username, pass)
        api.login(request)
    }
}
