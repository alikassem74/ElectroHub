package com.example.electrohub.repositories

import android.util.Log
import com.example.electrohub.models.Products
import com.example.electrohub.models.SavedProducts
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedProductsRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // =========================================================
    // GET CURRENT USER ID
    // =========================================================

    private fun getUserId(): String? {

        return auth.currentUser?.uid
    }


    // =========================================================
    // SAVE PRODUCT
    // =========================================================

    suspend fun saveProduct(
        productId: String
    ): Boolean {

        val userId =
            getUserId()
                ?: return false

        return try {

            val savedProduct =
                SavedProducts(
                    productId = productId,
                    savedAt = Timestamp.now()
                )


            firestore
                .collection("users")
                .document(userId)
                .collection("savedProducts")
                .document(productId)
                .set(savedProduct)
                .await()


            Log.d(
                "SAVE_DEBUG",
                "Product saved successfully"
            )

            true

        } catch (e: Exception) {

            Log.e(
                "SAVE_DEBUG",
                "Save failed",
                e
            )

            false
        }
    }


    // =========================================================
    // REMOVE SAVED PRODUCT
    // =========================================================

    suspend fun removeSavedProduct(
        productId: String
    ): Boolean {

        val userId =
            getUserId()
                ?: return false

        return try {

            firestore
                .collection("users")
                .document(userId)
                .collection("savedProducts")
                .document(productId)
                .delete()
                .await()


            true

        } catch (e: Exception) {

            Log.e(
                "SAVE_DEBUG",
                "Remove failed",
                e
            )

            false
        }
    }


    // =========================================================
    // CHECK IF PRODUCT IS SAVED
    // =========================================================

    suspend fun isProductSaved(
        productId: String
    ): Boolean {

        val userId =
            getUserId()
                ?: return false

        return try {

            val document =
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("savedProducts")
                    .document(productId)
                    .get()
                    .await()


            document.exists()

        } catch (e: Exception) {

            Log.e(
                "SAVE_DEBUG",
                "Failed to check saved product",
                e
            )

            false
        }
    }


    // =========================================================
    // TOGGLE SAVED PRODUCT
    // =========================================================

    suspend fun toggleSavedProduct(
        productId: String
    ): Boolean {

        val isSaved =
            isProductSaved(productId)


        return if (isSaved) {

            removeSavedProduct(productId)

            false

        } else {

            saveProduct(productId)

            true
        }
    }


    // =========================================================
    // GET SAVED PRODUCTS - REAL TIME
    // =========================================================

    fun getSavedProducts(): Flow<List<Products>> =
        callbackFlow {

            val userId =
                getUserId()


            if (userId == null) {

                trySend(emptyList())

                close()

                return@callbackFlow
            }


            val listener =
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("savedProducts")
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {

                            close(error)

                            return@addSnapshotListener
                        }


                        if (snapshot == null) {

                            trySend(emptyList())

                            return@addSnapshotListener
                        }


                        val productIds =
                            snapshot.documents
                                .map {
                                    it.id
                                }


                        if (productIds.isEmpty()) {

                            trySend(emptyList())

                            return@addSnapshotListener
                        }


                        // Fetch products asynchronously
                        // from Firestore.

                        kotlinx.coroutines.CoroutineScope(
                            kotlinx.coroutines.Dispatchers.IO
                        ).launch {

                            try {

                                val products =
                                    productIds.mapNotNull { id ->

                                        val document =
                                            firestore
                                                .collection("products")
                                                .document(id)
                                                .get()
                                                .await()


                                        if (
                                            document.exists()
                                        ) {

                                            document
                                                .toObject(
                                                    Products::class.java
                                                )
                                                ?.copy(
                                                    productId =
                                                        document.id
                                                )

                                        } else {

                                            null
                                        }
                                    }


                                trySend(products)

                            } catch (e: Exception) {

                                close(e)
                            }
                        }
                    }


            awaitClose {

                listener.remove()
            }
        }
}