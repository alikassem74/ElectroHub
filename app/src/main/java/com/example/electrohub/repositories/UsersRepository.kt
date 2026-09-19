package com.example.electrohub.repositories

import android.util.Log
import com.example.electrohub.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class UsersRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? {

        return auth.currentUser?.uid
    }

    suspend fun saveUser(
        uid: String,
        user: Users
    ): Boolean {

        return try {

            firestore
                .collection("users")
                .document(uid)
                .set(user)
                .await()

            true

        } catch (e: Exception) {

            logError(
                "Failed to save user",
                e
            )

            false
        }
    }

    suspend fun getUserById(
        uid: String
    ): Users? {

        return try {

            firestore
                .collection("users")
                .document(uid)
                .get()
                .await()
                .toObject(Users::class.java)

        } catch (e: Exception) {

            logError(
                "Failed to get user",
                e
            )

            null
        }
    }

    suspend fun updateFcmToken(
        token: String
    ): Boolean {

        val uid =
            getCurrentUserId()
                ?: return false

        return try {

            firestore
                .collection("users")
                .document(uid)
                .update(
                    "fcmToken",
                    token
                )
                .await()

            true

        } catch (e: Exception) {

            logError(
                "Failed to update FCM token",
                e
            )

            false
        }
    }

    suspend fun saveCurrentFcmToken(): Boolean {

        return try {

            val token =
                FirebaseMessaging
                    .getInstance()
                    .token
                    .await()

            updateFcmToken(token)

        } catch (e: Exception) {

            logError(
                "Failed to get FCM token",
                e
            )

            false
        }
    }

    private fun logError(
        message: String,
        exception: Exception
    ) {
            Log.e(
                "UsersRepository",
                message,
                exception
            )
    }
}