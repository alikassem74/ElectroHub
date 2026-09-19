package com.example.electrohub.models

import com.google.firebase.Timestamp

data class Users(
    var name: String? = "",
    var email: String? = "",
    var createdAt: Timestamp? = null,
    var profileImage: String? = "",
    var fcmToken: String? = "",
    val country: String = "",
    val region: String = "",
    var warnings: Long = 0,
    var isBlocked: Boolean = false
)
