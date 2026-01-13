
package com.example.nextcloud_passwords_wearos.di

import com.example.nextcloud_passwords_wearos.data.local.TokenManager
import com.example.nextcloud_passwords_wearos.data.remote.NextcloudApi
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.example.nextcloud_passwords_wearos.ui.login.LoginViewModel
import com.google.android.gms.wearable.Wearable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        TokenManager(androidContext())
    }
    single {
        val logging = HttpLoggingInterceptor()
        // Reduce logging level to improve performance on Wear OS
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("http://localhost/") // Placeholder, overridden by dynamic URLs
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<NextcloudApi> {
        get<Retrofit>().create(NextcloudApi::class.java)
    }
    single {
        PasswordRepository(get(), get())
    }
    
    single { Wearable.getMessageClient(androidContext()) }
    single { Wearable.getNodeClient(androidContext()) }
    
    viewModel {
        LoginViewModel(get(), get(), get())
    }
}
