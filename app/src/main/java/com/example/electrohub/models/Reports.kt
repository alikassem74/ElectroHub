package com.example.electrohub.models

import com.google.firebase.Timestamp

data class Reports(
    val reportId: String = "",
    val reporterId: String = "",
    val productId: String = "",
    val productOwnerId: String = "",
    val productName: String = "",
    val reason: String = "",
    val description: String = "",
    val timestamp: Timestamp? = null,
    val status: String = "pending",
    val adminAction: String = ""
)