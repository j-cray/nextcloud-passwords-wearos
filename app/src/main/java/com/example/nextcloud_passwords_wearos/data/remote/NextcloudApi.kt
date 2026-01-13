
package com.example.nextcloud_passwords_wearos.data.remote

import com.example.nextcloud_passwords_wearos.data.model.Password
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface NextcloudApi {
    @POST
    suspend fun login(@Url url: String, @Body request: LoginRequest): LoginResponse

    @GET
    suspend fun getPasswords(@Url url: String, @Header("Authorization") token: String): List<Password>
}
