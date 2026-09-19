package com.example.electrohub.repositories

import android.util.Log
import com.example.electrohub.models.Products
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductDetailsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getProduct(
        productId: String
    ): Products? {

        return try {

            val document =
                firestore
                    .collection("products")
                    .document(productId)
                    .get()
                    .await()

            document
                .toObject(Products::class.java)
                ?.copy(
                    productId = document.id
                )

        } catch (e: Exception) {

            Log.e(
                "ProductDetails",
                "Failed to get product",
                e
            )

            null
        }
    }
}