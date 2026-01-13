
package com.example.nextcloud_passwords_wearos.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        return if (token.isNullOrEmpty()) null else token
    }
    
    fun saveServerUrl(url: String) {
        sharedPreferences.edit().putString("server_url", url).apply()
    }
    
    fun getServerUrl(): String? {
        return sharedPreferences.getString("server_url", null)
    }
    
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
