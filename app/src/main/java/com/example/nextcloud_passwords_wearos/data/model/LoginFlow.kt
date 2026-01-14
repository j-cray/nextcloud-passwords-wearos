
package com.example.nextcloud_passwords_wearos.data.model

import com.google.gson.annotations.SerializedName

data class LoginFlowInitResponse(
    val poll: PollEndpoint,
    val login: String
)

data class PollEndpoint(
    val token: String,
    val endpoint: String
)

data class LoginFlowPollResponse(
    val server: String,
    val loginName: String,
    val appPassword: String
)
