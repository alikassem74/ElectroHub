package com.example.electrohub.models

import com.google.firebase.Timestamp

data class Products(
    var productId: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var imageUrls: List<String> = emptyList(),
    var imagePublicIds: List<String> = emptyList(),
    var ownerId: String = "",
    var ownerName: String = "",
    var country: String = "",
    var region: String = "",
    val isVisible: Boolean = true,
    var createdAt: Timestamp? = null
)