package com.example.electrohub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.electrohub.models.ChatDisplay
import com.example.electrohub.repositories.MyChatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyChatsViewModel : ViewModel() {

    private val _repository =
        MyChatsRepository()


    // =========================================================
    // MY CHATS
    // =========================================================

    private val _myChats =
        MutableStateFlow<List<ChatDisplay>>(emptyList())

    val myChats: StateFlow<List<ChatDisplay>> =
        _myChats.asStateFlow()


    // =========================================================
    // LOAD MY CHATS
    // =========================================================

    fun loadMyChats() {

        viewModelScope.launch {

            val chats =
                _repository.getMyChats()

            _myChats.value =
                chats
        }
    }


    // =========================================================
    // HIDE CHAT
    // =========================================================

    fun hideChat(
        chatId: String,
        onResult: (Boolean) -> Unit
    ) {

        viewModelScope.launch {

            val success =
                _repository.hideChat(chatId)

            onResult(success)

            // Refresh the list after hiding
            if (success) {

                loadMyChats()
            }
        }
    }
}