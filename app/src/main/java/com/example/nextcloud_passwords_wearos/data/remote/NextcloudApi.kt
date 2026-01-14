
package com.example.nextcloud_passwords_wearos.data.remote

import com.example.nextcloud_passwords_wearos.data.model.LoginFlowInitResponse
import com.example.nextcloud_passwords_wearos.data.model.LoginFlowPollResponse
import com.example.nextcloud_passwords_wearos.data.model.Password
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface NextcloudApi {
    @Headers("OCS-APIRequest: true")
    @GET
    suspend fun getPasswords(@Url url: String, @Header("Authorization") authHeader: String): List<Password>
    
    @POST
    suspend fun initLoginFlow(@Url url: String): LoginFlowInitResponse
    
    @FormUrlEncoded
    @POST
    suspend fun pollLoginFlow(@Url url: String, @Field("token") token: String): LoginFlowPollResponse
}
