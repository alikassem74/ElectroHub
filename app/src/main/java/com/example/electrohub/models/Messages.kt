package com.example.electrohub.models

import com.google.firebase.Timestamp

data class Messages(
    var senderId: String = "",
    var message: String = "",
    var timestamp: Timestamp? = null
)
