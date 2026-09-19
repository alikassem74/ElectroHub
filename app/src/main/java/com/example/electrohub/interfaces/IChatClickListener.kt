package com.example.electrohub.interfaces

import com.example.electrohub.models.Chat
import com.example.electrohub.models.ChatDisplay

interface IChatClickListener {
    fun onChatClick(chat: ChatDisplay)

    fun onDeleteChat(chat: ChatDisplay)
}