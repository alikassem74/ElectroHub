package com.example.electrohub.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.electrohub.base.CloudinaryConfig
import com.example.electrohub.base.DataStoreManager
import com.example.electrohub.models.CloudinaryImage
import com.example.electrohub.models.Products
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.google.firebase.firestore.Query


class ProductsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val productsCollection = firestore.collection("products")

    // ADD PRODUCT
    suspend fun addProduct(
        product: Products
    ): Result<Unit> {

        return try {

            productsCollection
                .add(product)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "AddProduct",
                "Failed to add product",
                e
            )

            Result.failure(e)
        }
    }


    // GET PRODUCTS - REAL TIME
    fun getProducts(
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Flow<List<Products>> =
        callbackFlow {

            var query: Query = productsCollection

            if (category != null) {
                query = query.whereEqualTo("category", category)
            }

            if (minPrice != null && maxPrice !=null) {
                query = query.whereGreaterThanOrEqualTo("price", minPrice)
                query = query.whereLessThanOrEqualTo("price", maxPrice)
            }
            if (minPrice != null && maxPrice ==null) {
                query = query.whereGreaterThanOrEqualTo("price", minPrice)
            }


            val listenerRegistration =
                query.addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        Log.e(
                            "GetProducts",
                            "Failed to get products",
                            error
                        )

                        return@addSnapshotListener
                    }

                    val products =
                        snapshot
                            ?.documents
                            ?.mapNotNull { document ->
                                document
                                    .toObject(Products::class.java)
                                    ?.copy(productId = document.id)
                            }
                            ?: emptyList()

                    Log.d(
                        "GetProducts",
                        "Products: ${products.size}"
                    )

                    trySend(products)
                }

            awaitClose {
                listenerRegistration.remove()
            }
        }

    // GET OWNER DATA
    suspend fun getOwnerData(
        context: Context
    ): Pair<String, String> {

        val id =
            DataStoreManager.getUserId(
                context,
                stringPreferencesKey(
                    DataStoreManager.PREF_KEY_ID
                )
            )

        val name =
            DataStoreManager.getUsername(
                context,
                stringPreferencesKey(
                    DataStoreManager.PREF_KEY_USERNAME
                )
            )

        return Pair(
            id,
            name
        )
    }


    // UPLOAD IMAGES
    suspend fun uploadImages(
        images: List<Uri>
    ): List<CloudinaryImage> {

        val uploadedImages =
            mutableListOf<CloudinaryImage>()


        for (image in images) {

            val uploadedImage =
                suspendCancellableCoroutine<CloudinaryImage?> { continuation ->

                    MediaManager
                        .get()
                        .upload(image)
                        .unsigned(
                            CloudinaryConfig.UPLOAD_PRESET
                        )
                        .callback(
                            object : UploadCallback {

                                override fun onStart(
                                    requestId: String?
                                ) {
                                }

                                override fun onProgress(
                                    requestId: String?,
                                    bytes: Long,
                                    totalBytes: Long
                                ) {
                                }

                                override fun onSuccess(
                                    requestId: String?,
                                    resultData: MutableMap<Any?, Any?>
                                ) {

                                    val url =
                                        resultData["secure_url"]
                                                as? String

                                    val publicId =
                                        resultData["public_id"]
                                                as? String


                                    if (
                                        url != null &&
                                        publicId != null
                                    ) {

                                        continuation.resume(
                                            CloudinaryImage(
                                                url = url,
                                                publicId = publicId
                                            )
                                        )

                                    } else {

                                        continuation.resume(
                                            null
                                        )
                                    }
                                }

                                override fun onError(
                                    requestId: String?,
                                    error: ErrorInfo?
                                ) {

                                    continuation.resume(
                                        null
                                    )
                                }

                                override fun onReschedule(
                                    requestId: String?,
                                    error: ErrorInfo?
                                ) {
                                }
                            }
                        )
                        .dispatch()
                }


            uploadedImage?.let {

                uploadedImages.add(it)
            }
        }

        return uploadedImages
    }

    // DELETE PRODUCT
    suspend fun deleteProduct(
        product: Products
    ): Result<Unit> {

        return try {

            productsCollection
                .document(product.productId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "DELETE_PRODUCT",
                "Failed to delete product",
                e
            )

            Result.failure(e)
        }
    }



    // GET PRODUCT BY ID
    suspend fun getProductById(
        productId: String
    ): Products? {

        return try {

            val document =
                productsCollection
                    .document(productId)
                    .get()
                    .await()


            document
                .toObject(
                    Products::class.java
                )
                ?.copy(
                    productId =
                        document.id
                )

        } catch (e: Exception) {

            Log.e(
                "GetProduct",
                "Failed to get product",
                e
            )

            null
        }
    }


    // UPDATE PRODUCT
    suspend fun updateProduct(
        product: Products,
        deletedImagePublicIds: List<String>
    ): Result<Unit> {

        return try {

            // UPDATE FIRESTORE
            productsCollection
                .document(product.productId)
                .set(product)
                .await()


            /*
             * IMPORTANT:
             *
             * We do NOT delete Cloudinary images here.
             *
             * Cloudinary deletion requires secure server-side
             * credentials and should not be performed directly
             * from the Android application.
             *
             * The deleted images are already removed from the
             * product's imageUrls and imagePublicIds in Firestore.
             */


            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "UpdateProduct",
                "Failed to update product",
                e
            )

            Result.failure(e)
        }
    }
    //PRODUCTS FOR HOME PAGE
    fun getRecentProducts(): Flow<List<Products>> =
        callbackFlow {

            val listenerRegistration =
                productsCollection
                    .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                    )
                    .limit(12)
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {

                            trySend(emptyList())

                            return@addSnapshotListener
                        }

                        val products =
                            snapshot
                                ?.documents
                                ?.mapNotNull { document ->

                                    document
                                        .toObject(
                                            Products::class.java
                                        )
                                        ?.copy(
                                            productId =
                                                document.id
                                        )
                                }
                                ?: emptyList()

                        trySend(products)
                    }

            awaitClose {
                listenerRegistration.remove()
            }
        }
}