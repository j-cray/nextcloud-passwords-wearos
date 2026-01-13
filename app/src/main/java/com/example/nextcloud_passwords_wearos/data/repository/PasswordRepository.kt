
package com.example.nextcloud_passwords_wearos.data.repository

import android.util.Base64
import com.example.nextcloud_passwords_wearos.data.local.TokenManager
import com.example.nextcloud_passwords_wearos.data.model.Password
import com.example.nextcloud_passwords_wearos.data.remote.NextcloudApi

class PasswordRepository(
    private val api: NextcloudApi,
    private val tokenManager: TokenManager
) {
    suspend fun login(serverUrl: String, username: String, pass: String) {
        val baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
        val listUrl = "${baseUrl}index.php/apps/passwords/api/1.0/password/list"
        
        // Create Basic Auth Header
        val credentials = "$username:$pass"
        val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        
        // Verify credentials by making a request
        api.getPasswords(listUrl, authHeader)
        
        // If successful (no exception thrown), save credentials
        tokenManager.saveToken(authHeader)
        tokenManager.saveServerUrl(baseUrl)
    }

    suspend fun getPasswords(): List<Password> {
        val authHeader = tokenManager.getToken() ?: throw IllegalStateException("Not logged in")
        val baseUrl = tokenManager.getServerUrl() ?: throw IllegalStateException("Server URL not set")
        val listUrl = "${baseUrl}index.php/apps/passwords/api/1.0/password/list"
        
        val passwords = api.getPasswords(listUrl, authHeader)
        
        // Filter out CSE encrypted passwords for now as we don't support decryption
        return passwords.filter { it.cseType == "none" }
    }
    
    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
    
    fun logout() {
        tokenManager.clear()
    }
}
