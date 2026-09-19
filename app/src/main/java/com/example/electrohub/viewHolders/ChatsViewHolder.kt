package com.example.electrohub.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemChatBinding
import com.example.electrohub.interfaces.IChatClickListener
import com.example.electrohub.models.Chat
import com.example.electrohub.models.ChatDisplay


class ChatsViewHolder(
    private val binding: ItemChatBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(chat: ChatDisplay,
             inter: IChatClickListener?
    ){
        binding.txtVLastMessage.text =
            chat.lastMessage


        // We will load the other user's name later
        // after we fetch users from Firestore
        binding.txtVUserName.text =
            chat.userName

        itemView.setOnClickListener {

            inter?.onChatClick(chat)

        }



        // Delete Chat

        binding.imgDeleteChat.setOnClickListener {

            inter?.onDeleteChat(chat)

        }
    }

}