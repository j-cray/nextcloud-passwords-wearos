
package com.example.nextcloud_passwords_wearos.data.repository

import android.util.Base64
import com.example.nextcloud_passwords_wearos.data.local.TokenManager
import com.example.nextcloud_passwords_wearos.data.model.LoginFlowInitResponse
import com.example.nextcloud_passwords_wearos.data.model.LoginFlowPollResponse
import com.example.nextcloud_passwords_wearos.data.model.Password
import com.example.nextcloud_passwords_wearos.data.remote.NextcloudApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PasswordRepository(
    private val api: NextcloudApi,
    private val tokenManager: TokenManager
) {
    private val _loginEvent = MutableSharedFlow<Unit>()
    val loginEvent = _loginEvent.asSharedFlow()
    
    private var cachedPasswords: List<Password> = emptyList()

    suspend fun login(serverUrl: String, username: String, pass: String) {
        val baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
        val listUrl = "${baseUrl}index.php/apps/passwords/api/1.0/password/list"
        
        val credentials = "$username:$pass"
        val authHeader = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        
        api.getPasswords(listUrl, authHeader)
        
        tokenManager.saveToken(authHeader)
        tokenManager.saveServerUrl(baseUrl)
        
        _loginEvent.emit(Unit)
    }
    
    suspend fun initLoginFlow(serverUrl: String): LoginFlowInitResponse {
        val baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
        val url = "${baseUrl}index.php/login/v2"
        return api.initLoginFlow(url)
    }
    
    suspend fun pollLoginFlow(endpoint: String, token: String): LoginFlowPollResponse {
        return api.pollLoginFlow(endpoint, token)
    }

    suspend fun getPasswords(): List<Password> {
        val authHeader = tokenManager.getToken() ?: throw IllegalStateException("Not logged in")
        val baseUrl = tokenManager.getServerUrl() ?: throw IllegalStateException("Server URL not set")
        val listUrl = "${baseUrl}index.php/apps/passwords/api/1.0/password/list"
        
        val passwords = api.getPasswords(listUrl, authHeader)
        cachedPasswords = passwords.filter { it.cseType == "none" }
        return cachedPasswords
    }
    
    suspend fun createPassword(password: Password) {
        val authHeader = tokenManager.getToken() ?: throw IllegalStateException("Not logged in")
        val baseUrl = tokenManager.getServerUrl() ?: throw IllegalStateException("Server URL not set")
        val createUrl = "${baseUrl}index.php/apps/passwords/api/1.0/password/create"
        
        api.createPassword(createUrl, authHeader, password)
    }
    
    fun getPassword(id: String): Password? {
        return cachedPasswords.find { it.id == id }
    }
    
    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
    
    fun logout() {
        tokenManager.clear()
        cachedPasswords = emptyList()
    }
}
