
package com.example.nextcloud_passwords_wearos

import android.app.Application
import com.example.nextcloud_passwords_wearos.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NextcloudPasswordsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NextcloudPasswordsApp)
            modules(appModule)
        }
    }
}
