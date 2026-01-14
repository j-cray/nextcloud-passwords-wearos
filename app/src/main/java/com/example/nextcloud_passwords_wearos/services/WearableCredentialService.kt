
package com.example.nextcloud_passwords_wearos.services

import android.content.Intent
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WearableCredentialService : WearableListenerService(), KoinComponent {

    private val repository: PasswordRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/credentials") {
            val data = String(messageEvent.data)
            val parts = data.split("|")
            if (parts.size == 3) {
                val server = parts[0]
                val user = parts[1]
                val pass = parts[2]
                
                scope.launch {
                    try {
                        repository.login(server, user, pass)
                        // Notify UI
                        val intent = Intent("com.example.nextcloud_passwords_wearos.LOGIN_SUCCESS")
                        intent.setPackage(packageName)
                        sendBroadcast(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
