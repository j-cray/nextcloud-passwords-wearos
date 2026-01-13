
package com.example.nextcloud_passwords_wearos.data.model

data class Password(
    val id: String,
    val label: String,
    val username: String,
    val password: String, // In a real app, this might be encrypted or fetched separately
    val url: String?
)
