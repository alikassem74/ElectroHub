package com.example.electrohub.repositories

import com.example.electrohub.models.Chat
import com.example.electrohub.models.Messages
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val chatsCollection =
        firestore.collection("chats")


    // =========================================================
    // CREATE CHAT
    // =========================================================

    suspend fun createChat(
        chat: Chat
    ): String? {

        return try {

            val document = chatsCollection.document()

            document
                .set(chat)
                .await()

            document.id

        } catch (e: Exception) {

            null
        }
    }


    // =========================================================
    // SEND MESSAGE
    // =========================================================

    suspend fun sendMessage(
        chatId: String,
        message: Messages
    ): Boolean {

        return try {

            chatsCollection
                .document(chatId)
                .collection("messages")
                .add(message)
                .await()


            chatsCollection
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to message.message,
                        "lastMessageTime" to Timestamp.now()
                    )
                )
                .await()

            true

        } catch (e: Exception) {

            false
        }
    }


    // =========================================================
    // GET MESSAGES - REAL TIME
    // =========================================================

    fun getMessages(
        chatId: String
    ): Flow<List<Messages>> = callbackFlow {

        val listener =
            chatsCollection
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        close(error)

                        return@addSnapshotListener
                    }


                    val messages =
                        snapshot
                            ?.toObjects(Messages::class.java)
                            ?: emptyList()


                    trySend(messages)
                }


        awaitClose {
            listener.remove()
        }
    }


    // =========================================================
    // GET EXISTING CHAT
    // =========================================================

    suspend fun getExistingChat(
        productId: String,
        sellerId: String,
        buyerId: String
    ): String? {

        return try {

            val snapshot =
                chatsCollection
                    .whereEqualTo(
                        "productId",
                        productId
                    )
                    .whereEqualTo(
                        "sellerId",
                        sellerId
                    )
                    .whereEqualTo(
                        "buyerId",
                        buyerId
                    )
                    .get()
                    .await()


            if (!snapshot.isEmpty) {

                snapshot.documents[0].id

            } else {

                null
            }

        } catch (e: Exception) {

            null
        }
    }


    // =========================================================
    // GET CHAT BY ID
    // =========================================================

    suspend fun getChatById(
        chatId: String
    ): Chat? {

        return try {

            val document =
                chatsCollection
                    .document(chatId)
                    .get()
                    .await()


            document
                .toObject(Chat::class.java)
                ?.copy(
                    chatId = document.id
                )

        } catch (e: Exception) {

            null
        }
    }
}