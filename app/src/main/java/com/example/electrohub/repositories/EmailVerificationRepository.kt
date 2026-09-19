package com.example.electrohub.repositories

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class EmailVerificationRepository {

    private val auth =
        FirebaseAuth.getInstance()

    suspend fun sendVerificationEmail(): Result<Unit> {

        return try {

            val user =
                auth.currentUser
                    ?: return Result.failure(
                        Exception("No user is currently logged in")
                    )

            user.sendEmailVerification()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun isEmailVerified(): Result<Boolean> {

        return try {

            val user =
                auth.currentUser
                    ?: return Result.success(false)

            user.reload()
                .await()

            Result.success(
                user.isEmailVerified
            )

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}
