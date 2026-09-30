package com.example.electrohub.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.electrohub.base.DataStoreManager
import com.example.electrohub.models.Users
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

            val firebaseUser = createFirebaseAccount(
                email,
                password
            )

            firebaseUser.sendEmailVerification().await()

            val user = createUser(
                name,
                email,
                country,
                region
            )

            saveUser(
                firebaseUser.uid,
                user
            )

            Result.success(firebaseUser.uid)

        } catch (e: Exception) {

            logRegistrationError(e)

            Result.failure(e)
        }
    }

    private suspend fun createFirebaseAccount(
        email: String,
        password: String
    ): FirebaseUser {

        val result = auth.createUserWithEmailAndPassword(
            email,
            password
        ).await()

        return result.user
            ?: throw Exception("User is null")
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