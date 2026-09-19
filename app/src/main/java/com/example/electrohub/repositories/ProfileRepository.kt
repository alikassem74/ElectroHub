package com.example.electrohub.repositories

import android.net.Uri
import android.util.Log

import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.electrohub.base.CloudinaryConfig
import com.example.electrohub.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class ProfileRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()

    // GET USER

    suspend fun getUser(): Users? {

        return try {

            val uid =
                auth.currentUser?.uid
                    ?: return null

            val document =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()

            document.toObject(
                Users::class.java
            )

        } catch (e: Exception) {

            Log.e(
                "ProfileRepository",
                e.message ?: "Failed to get user"
            )

            null
        }
    }

    // LOGOUT

    suspend fun logout(): Boolean {

        return try {

            auth.signOut()

            true

        } catch (e: Exception) {

            Log.e(
                "ProfileRepository",
                e.message ?: "Logout failed"
            )

            false
        }
    }

    // UPLOAD PROFILE IMAGE

    suspend fun uploadProfileImage(
        image: Uri
    ): String? {

        return suspendCancellableCoroutine { continuation ->

            MediaManager.get()
                .upload(image)
                .unsigned(
                    CloudinaryConfig.PROFILE_UPLOAD_PRESET
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

                            continuation.resume(url)
                        }

                        override fun onError(
                            requestId: String?,
                            error: ErrorInfo?
                        ) {

                            Log.e(
                                "ProfileUpload",
                                error?.description
                                    ?: "Unknown error"
                            )

                            continuation.resume(null)
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
    }

    // UPDATE PROFILE IMAGE

    suspend fun updateProfileImage(
        imageUrl: String
    ): Boolean {

        return try {

            val uid =
                auth.currentUser?.uid
                    ?: return false

            firestore
                .collection("users")
                .document(uid)
                .update(
                    "profileImage",
                    imageUrl
                )
                .await()

            true

        } catch (e: Exception) {

            Log.e(
                "ProfileUpdate",
                e.message ?: "Update failed"
            )

            false
        }
    }
}