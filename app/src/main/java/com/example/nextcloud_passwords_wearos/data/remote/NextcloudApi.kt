
package com.example.nextcloud_passwords_wearos.data.remote

import com.example.nextcloud_passwords_wearos.data.model.Password
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url

interface NextcloudApi {
    // We verify login by successfully fetching data.
    // In a real app, you might use a user info endpoint, but fetching the list (maybe with limit=1) is a good check.
    @Headers("OCS-APIRequest: true")
    @GET
    suspend fun getPasswords(@Url url: String, @Header("Authorization") authHeader: String): List<Password>
}
