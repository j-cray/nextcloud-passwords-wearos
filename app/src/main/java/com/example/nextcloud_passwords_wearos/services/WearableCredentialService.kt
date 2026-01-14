
package com.example.nextcloud_passwords_wearos.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.nextcloud_passwords_wearos.R
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
            showNotification("Received credentials...")
            
            val data = String(messageEvent.data)
            val parts = data.split("|")
            if (parts.size == 3) {
                val server = parts[0]
                val user = parts[1]
                val pass = parts[2]
                
                scope.launch {
                    try {
                        repository.login(server, user, pass)
                        showNotification("Login successful!")
                        
                        // Notify UI via Broadcast as backup
                        val intent = Intent("com.example.nextcloud_passwords_wearos.LOGIN_SUCCESS")
                        intent.setPackage(packageName)
                        sendBroadcast(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showNotification("Login failed: ${e.message}")
                    }
                }
            }
        }
    }
    
    private fun showNotification(message: String) {
        val channelId = "sync_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sync", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use system icon for now
            .setContentTitle("Nextcloud Passwords")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
            
        notificationManager.notify(1, notification)
    }
}
