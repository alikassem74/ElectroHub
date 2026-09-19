package com.example.electrohub.repositories

import com.example.electrohub.models.Products
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MyProductsRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // =========================================================
    // GET MY PRODUCTS
    // =========================================================

    suspend fun getMyProducts(): List<Products> {

        val userId =
            auth.currentUser?.uid
                ?: return emptyList()


        return try {

            val snapshot =
                firestore
                    .collection("products")
                    .whereEqualTo(
                        "ownerId",
                        userId
                    )
                    .get()
                    .await()


            snapshot.documents.mapNotNull { document ->

                document
                    .toObject(Products::class.java)
                    ?.copy(
                        productId = document.id
                    )
            }

        } catch (e: Exception) {

            emptyList()
        }
    }
}