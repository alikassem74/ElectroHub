package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.Chat
import com.example.electrohub.models.Messages
import com.example.electrohub.repositories.ChatRepository
import com.example.electrohub.repositories.UsersRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatRepository = ChatRepository()
    private val _userRepository = UsersRepository()


    // =========================================================
    // MESSAGES
    // =========================================================

    private val _messages =
        MutableStateFlow<List<Messages>>(emptyList())

    val messages: StateFlow<List<Messages>> =
        _messages.asStateFlow()


    // =========================================================
    // CHAT INFO
    // =========================================================

    private val _chatInfo =
        MutableStateFlow<Chat?>(null)

    val chatInfo: StateFlow<Chat?> =
        _chatInfo.asStateFlow()


    // =========================================================
    // SELLER NAME
    // =========================================================

    private val _sellerName =
        MutableStateFlow("User")

    val sellerName: StateFlow<String> =
        _sellerName.asStateFlow()


    // =========================================================
    // USER
    // =========================================================

    private val _userId = _userRepository.getCurrentUserId()

    private var _chatId = ""


    fun getCurrentUserId(): String {
        return _userId?:""
    }


    // =========================================================
    // SET CHAT ID
    // =========================================================

    fun setChatId(id: String) {

        _chatId = id

        loadMessages()
    }


    // =========================================================
    // LOAD MESSAGES
    // =========================================================

    private fun loadMessages() {

        if (_chatId.isEmpty()) {
            return
        }

        viewModelScope.launch {

            _chatRepository
                .getMessages(_chatId)
                .collect { messages ->

                    _messages.value = messages
                }
        }
    }


    // =========================================================
    // SEND MESSAGE
    // =========================================================

    fun sendMessage(text: String) {

        if (text.isEmpty() || _chatId.isEmpty()) {
            return
        }

        val message =
            Messages(
                senderId = _userId?:"Null Id",
                message = text,
                timestamp = Timestamp.now()
            )

        viewModelScope.launch {

            _chatRepository.sendMessage(
                _chatId,
                message
            )
        }
    }


    // =========================================================
    // CREATE CHAT
    // =========================================================

    fun createChat(
        chat: Chat,
        onResult: (String?) -> Unit
    ) {

        viewModelScope.launch {

            val chatId =
                _chatRepository.createChat(chat)

            chatId?.let {
                _chatId = it
            }

            onResult(chatId)
        }
    }


    // =========================================================
    // OPEN CHAT
    // =========================================================

    fun openChat(
        productId: String,
        productName: String,
        sellerId: String,
        sellerName: String,
        onResult: (String) -> Unit
    ) {

        viewModelScope.launch {

            val buyerId =
                getCurrentUserId()

            val existingChatId =
                _chatRepository.getExistingChat(
                    productId = productId,
                    sellerId = sellerId,
                    buyerId = buyerId
                )

            if (existingChatId != null) {

                onResult(existingChatId)

                return@launch
            }


            val chat =
                Chat(
                    productId = productId,
                    productName = productName,
                    sellerId = sellerId,
                    buyerId = buyerId
                )


            val newChatId =
                _chatRepository.createChat(chat)

            newChatId?.let {
                onResult(it)
            }
        }
    }


    // =========================================================
    // LOAD CHAT INFO
    // =========================================================

    fun loadChatInfo(
        chatId: String
    ) {

        viewModelScope.launch {

            val chat =
                _chatRepository.getChatById(chatId)

            _chatInfo.value = chat

            chat?.let {

                if (_userId == it.sellerId) {

                    // Current user is the seller
                    // Show the buyer's name
                    getOtherUserName(it.buyerId)

                } else {

                    // Current user is the buyer
                    // Show the seller's name
                    getOtherUserName(it.sellerId)
                }
            }
        }
    }


    // =========================================================
    // GET SELLER NAME
    // =========================================================

    private fun getOtherUserName(
        userId: String
    ) {

        viewModelScope.launch {

            val user = _userRepository.getUserById(userId)

            _sellerName.value = user?.name ?: "User"
        }
    }
}