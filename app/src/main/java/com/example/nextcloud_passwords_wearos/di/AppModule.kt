
package com.example.nextcloud_passwords_wearos.di

import com.example.nextcloud_passwords_wearos.data.remote.NextcloudApi
import com.example.nextcloud_passwords_wearos.data.repository.PasswordRepository
import com.example.nextcloud_passwords_wearos.ui.login.LoginViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("http://localhost/") // This will be replaced by the user's server URL
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<NextcloudApi> {
        get<Retrofit>().create(NextcloudApi::class.java)
    }
    single {
        PasswordRepository(get())
    }
    viewModel {
        LoginViewModel(get())
    }
}
