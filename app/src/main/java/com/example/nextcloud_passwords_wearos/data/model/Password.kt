
package com.example.nextcloud_passwords_wearos.data.model

import com.google.gson.annotations.SerializedName

data class Password(
    val id: String,
    val label: String,
    val username: String,
    val password: String,
    val url: String?,
    @SerializedName("cseType")
    val cseType: String? = "none"
)
