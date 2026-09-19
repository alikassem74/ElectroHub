package com.example.electrohub.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.electrohub.base.DataStoreManager
import com.example.electrohub.models.Users
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class RegisterRepository {

    private val auth = FirebaseAuth.getInstance()
    private val usersRepository = UsersRepository()

    suspend fun register(
        name: String,
        email: String,
        password: String,
        country: String,
        region: String
    ): Result<String> {

        return try {

            val uid = createFirebaseAccount(
                email,
                password
            )

            sendVerificationEmail()

            val user = createUser(
                name,
                email,
                country,
                region
            )

            saveUser(
                uid,
                user
            )

            Result.success(uid)

        } catch (e: Exception) {

            logRegistrationError(e)

            Result.failure(e)
        }
    }

    private suspend fun createFirebaseAccount(
        email: String,
        password: String
    ): String {

        val result =
            auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

        return result.user?.uid
            ?: throw Exception("User ID is null")
    }

    private suspend fun sendVerificationEmail() {

        auth.currentUser
            ?.sendEmailVerification()
            ?.await()
            ?: throw Exception(
                "No authenticated user"
            )
    }

    private fun createUser(
        name: String,
        email: String,
        country: String,
        region: String
    ): Users {

        return Users(
            name = name,
            email = email,
            createdAt = Timestamp.now(),
            country = country,
            region = region
        )
    }

    private suspend fun saveUser(
        uid: String,
        user: Users
    ) {

        val saved =
            usersRepository.saveUser(
                uid,
                user
            )

        if (!saved) {

            deleteFirebaseAccount()

            throw Exception(
                "Failed to save user"
            )
        }
    }

    private suspend fun deleteFirebaseAccount() {

        try {

            auth.currentUser
                ?.delete()
                ?.await()

        } catch (e: Exception) {

            logRegistrationError(e)
        }
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

    private fun logRegistrationError(
        exception: Exception
    ) {
            Log.e(
                "RegisterRepository",
                "Registration failed",
                exception
            )
    }
}