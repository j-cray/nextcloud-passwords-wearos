
package com.example.nextcloud_passwords_wearos.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("id")
    val username: String,
    @SerializedName("password")
    val pass: String
)
