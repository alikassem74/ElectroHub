package com.example.electrohub.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SplashRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    suspend fun checkUserLogin(): Int {

        return try {
            val user = auth.currentUser

            // NOT LOGGED IN
            if (user == null) {

                return 0
            }

            // RELOAD USER
            user.reload().await()

            // CHECK IF BLOCKED
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

                return 3
            }

            // CHECK EMAIL VERIFICATION
            if (user.isEmailVerified) {

                1

            } else {

                2
            }

        } catch (e: Exception) {

            e.printStackTrace()

            4
        }
    }
}