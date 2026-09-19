package com.example.electrohub.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.electrohub.base.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoginRepository {

    private val auth =
        FirebaseAuth.getInstance()

    private val firestore =
        FirebaseFirestore.getInstance()


    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()


            val user =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("User not found")
                    )


            user.reload().await()


            if (!user.isEmailVerified) {

                auth.signOut()

                return Result.failure(
                    EmailNotVerifiedException()
                )
            }


            val userSnapshot =
                firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()


            val isBlocked =
                userSnapshot
                    .getBoolean("isBlocked")
                    ?: false


            if (isBlocked) {

                auth.signOut()

                return Result.failure(
                    AccountBlockedException()
                )
            }


            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "LOGIN_ERROR",
                "Login failed",
                e
            )

            Result.failure(e)
        }
    }


    suspend fun getUsername(): String {

        val uid =
            auth.currentUser?.uid
                ?: return ""


        return try {

            val snapshot =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()


            snapshot.getString("name")
                ?: ""

        } catch (e: Exception) {

            Log.e(
                "GET_USERNAME",
                "Failed to get username",
                e
            )

            ""
        }
    }


    fun getCurrentUserId(): String {

        return auth.currentUser?.uid
            ?: ""
    }


    suspend fun saveUsername(
        context: Context,
        username: String
    ) {

        DataStoreManager.setUsername(
            context,
            username,
            stringPreferencesKey(
                DataStoreManager.PREF_KEY_USERNAME
            )
        )
    }


    suspend fun saveUserId(
        context: Context,
        userId: String
    ) {

        DataStoreManager.setUserId(
            context,
            userId,
            stringPreferencesKey(
                DataStoreManager.PREF_KEY_ID
            )
        )
    }


    suspend fun resetPassword(
        email: String
    ): Result<Unit> {

        return try {

            auth.sendPasswordResetEmail(
                email
            ).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}


class EmailNotVerifiedException :
    Exception("Email not verified")


class AccountBlockedException :
    Exception("Account blocked")