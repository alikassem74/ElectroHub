package com.example.electrohub.models
import com.google.firebase.Timestamp

data class SavedProducts(
    val productId: String = "",
    val savedAt: Timestamp? = null
)
