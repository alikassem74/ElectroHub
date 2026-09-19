package com.example.electrohub.models

import com.google.firebase.Timestamp

data class Chat(
    val chatId: String = "",
    val productId: String = "",
    val productName: String = "",
    val sellerId: String = "",
    val buyerId: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Timestamp? = null,
    val hiddenFor: List<String> = emptyList()
)
