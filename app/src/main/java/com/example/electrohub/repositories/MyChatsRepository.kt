package com.example.electrohub.repositories

import com.example.electrohub.models.ChatDisplay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MyChatsRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    private val userRepository =
        UsersRepository()


    // =========================================================
    // GET MY CHATS
    // =========================================================

    suspend fun getMyChats(): List<ChatDisplay> {

        val userId =
            auth.currentUser?.uid
                ?: return emptyList()


        return try {

            val chats =
                ArrayList<ChatDisplay>()


            // -------------------------------------------------
            // CHATS WHERE CURRENT USER IS BUYER
            // -------------------------------------------------

            val buyerSnapshot =
                firestore
                    .collection("chats")
                    .whereEqualTo(
                        "buyerId",
                        userId
                    )
                    .get()
                    .await()


            // -------------------------------------------------
            // CHATS WHERE CURRENT USER IS SELLER
            // -------------------------------------------------

            val sellerSnapshot =
                firestore
                    .collection("chats")
                    .whereEqualTo(
                        "sellerId",
                        userId
                    )
                    .get()
                    .await()


            // -------------------------------------------------
            // COMBINE BOTH RESULTS
            // -------------------------------------------------

            val documents =
                buyerSnapshot.documents +
                        sellerSnapshot.documents


            // -------------------------------------------------
            // LOAD CHAT INFORMATION
            // -------------------------------------------------

            for (document in documents) {

                val hiddenFor =
                    document.get("hiddenFor")
                            as? List<String>
                        ?: emptyList()


                // Chat hidden by current user
                if (hiddenFor.contains(userId)) {
                    continue
                }


                val buyerId =
                    document.getString("buyerId")
                        ?: ""


                val sellerId =
                    document.getString("sellerId")
                        ?: ""


                val otherUserId =
                    if (buyerId == userId) {
                        sellerId
                    } else {
                        buyerId
                    }


                val user =
                    userRepository.getUserById(
                        otherUserId
                    )


                chats.add(
                    ChatDisplay(
                        chatId = document.id,

                        userName =
                            user?.name
                                ?: "User",

                        profileImage =
                            user?.profileImage
                                ?: "",

                        lastMessage =
                            document.get("lastMessage")
                                ?.toString()
                                ?: ""
                    )
                )
            }


            chats

        } catch (e: Exception) {

            emptyList()
        }
    }


    // =========================================================
    // HIDE CHAT
    // =========================================================

    suspend fun hideChat(
        chatId: String
    ): Boolean {

        val userId =
            auth.currentUser?.uid
                ?: return false


        return try {

            val document =
                firestore
                    .collection("chats")
                    .document(chatId)
                    .get()
                    .await()


            val hiddenFor =
                (
                        document.get("hiddenFor")
                                as? List<String>
                        )?.toMutableList()
                    ?: mutableListOf()


            if (!hiddenFor.contains(userId)) {

                hiddenFor.add(userId)
            }


            document.reference
                .update(
                    "hiddenFor",
                    hiddenFor
                )
                .await()


            true

        } catch (e: Exception) {

            false
        }
    }
}