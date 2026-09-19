package com.example.electrohub.viewHolders

import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemMessageReceivedBinding
import com.example.electrohub.models.Messages


class ReceivedMessageViewHolder(
    private val binding: ItemMessageReceivedBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Messages) {
        binding.txtVMessage.text = message.message
    }
}