
package com.example.nextcloud_passwords_wearos.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        masterKeyAlias,
        context,
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
