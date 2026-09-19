package com.example.electrohub.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemMessageSentBinding
import com.example.electrohub.models.Messages

class SentMessageViewHolder(
    private val binding: ItemMessageSentBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Messages) {
        binding.txtVMessage.text = message.message
    }
}