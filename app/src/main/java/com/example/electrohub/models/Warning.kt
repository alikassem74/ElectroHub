package com.example.electrohub.models

import com.google.firebase.Timestamp

data class Warning(
    val warningId: String = "",
    val type: String = "",
    val targetUserId: String = "",
    val reportId: String = "",
    val productName: String = "",
    val message: String = "",
    val createdAt: Timestamp? = null
)