package com.example.electrohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemMessageReceivedBinding
import com.example.electrohub.databinding.ItemMessageSentBinding
import com.example.electrohub.models.Messages
import com.example.electrohub.viewHolders.ReceivedMessageViewHolder
import com.example.electrohub.viewHolders.SentMessageViewHolder


class MessagesAdapter(
    private val messages: ArrayList<Messages>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val SENT_MESSAGE = 1
        private const val RECEIVED_MESSAGE = 2
    }
    fun updateMessages(
        newMessages: ArrayList<Messages>
    ){
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
    override fun getItemViewType(position: Int): Int {
        return if(messages[position].senderId == currentUserId){
            SENT_MESSAGE
        }else{
            RECEIVED_MESSAGE
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if(viewType == SENT_MESSAGE){
            val binding =
                ItemMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            SentMessageViewHolder(binding)
        }else{
            val binding =
                ItemMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ReceivedMessageViewHolder(binding)
        }
    }
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val message = messages[position]
        if(holder is SentMessageViewHolder){
            holder.bind(message)
        }
        if(holder is ReceivedMessageViewHolder){
            holder.bind(message)
        }
    }
    override fun getItemCount(): Int {
        return messages.size
    }
}