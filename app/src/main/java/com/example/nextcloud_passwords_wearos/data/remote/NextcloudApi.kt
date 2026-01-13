
package com.example.nextcloud_passwords_wearos.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface NextcloudApi {
    @POST("login") // Replace with the actual login endpoint
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
