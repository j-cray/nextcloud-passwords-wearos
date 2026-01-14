
package com.example.nextcloud_passwords_wearos.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/wear-credentials") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val server = dataMap.getString("server")
                val user = dataMap.getString("user")
                val pass = dataMap.getString("password")
                
                if (server != null && user != null && pass != null) {
                    showNotification("Received credentials via Data Layer...")
                    scope.launch {
                        try {
                            repository.login(server, user, pass)
                            showNotification("Login successful!")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showNotification("Login failed: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        // Keep message listener as backup
        if (messageEvent.path == "/credentials") {
            showNotification("Received credentials via Message...")
            
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nextcloud Passwords")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
            
        notificationManager.notify(1, notification)
    }
}
