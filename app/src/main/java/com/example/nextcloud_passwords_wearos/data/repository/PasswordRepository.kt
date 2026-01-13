
package com.example.nextcloud_passwords_wearos.data.repository

import com.example.nextcloud_passwords_wearos.data.local.TokenManager
import com.example.nextcloud_passwords_wearos.data.model.Password
import com.example.nextcloud_passwords_wearos.data.remote.LoginRequest
import com.example.nextcloud_passwords_wearos.data.remote.NextcloudApi

class PasswordRepository(
    private val api: NextcloudApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(serverUrl: String, username: String, pass: String) {
        val baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
        val loginUrl = "${baseUrl}index.php/apps/passwords/api/1.0/session/request"
        
        val request = LoginRequest(username, pass)
        val response = api.login(loginUrl, request)
        
        tokenManager.saveToken(response.token)
        tokenManager.saveServerUrl(baseUrl)
    }

    suspend fun getPasswords(): List<Password> {
        val token = tokenManager.getToken() ?: throw IllegalStateException("Not logged in")
        val baseUrl = tokenManager.getServerUrl() ?: throw IllegalStateException("Server URL not set")
        val listUrl = "${baseUrl}index.php/apps/passwords/api/1.0/password/list"
        
        return api.getPasswords(listUrl, "Bearer $token")
    }
    
    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
    
    fun logout() {
        tokenManager.clear()
    }
}
