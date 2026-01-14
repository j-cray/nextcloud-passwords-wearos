
package com.example.nextcloud_passwords_wearos.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(private val context: Context) {

    private val masterKeyAlias by lazy { MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC) }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    private var cachedToken: String? = null
    private var cachedServerUrl: String? = null

    fun saveToken(token: String) {
        cachedToken = token
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        if (cachedToken != null) return cachedToken
        
        val token = sharedPreferences.getString("auth_token", null)
        cachedToken = if (token.isNullOrEmpty()) null else token
        return cachedToken
    }
    
    fun saveServerUrl(url: String) {
        cachedServerUrl = url
        sharedPreferences.edit().putString("server_url", url).apply()
    }
    
    fun getServerUrl(): String? {
        if (cachedServerUrl != null) return cachedServerUrl
        
        cachedServerUrl = sharedPreferences.getString("server_url", null)
        return cachedServerUrl
    }
    
    fun getTheme(): String {
        return sharedPreferences.getString("app_theme", "system") ?: "system"
    }
    
    fun saveTheme(theme: String) {
        sharedPreferences.edit().putString("app_theme", theme).apply()
    }
    
    fun clear() {
        cachedToken = null
        cachedServerUrl = null
        sharedPreferences.edit().clear().apply()
    }
}
