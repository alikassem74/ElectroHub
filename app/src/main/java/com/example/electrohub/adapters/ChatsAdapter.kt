package com.example.electrohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electrohub.databinding.ItemChatBinding
import com.example.electrohub.models.Chat
import com.example.electrohub.interfaces.IChatClickListener
import com.example.electrohub.models.ChatDisplay
import com.example.electrohub.viewHolders.ChatsViewHolder

class ChatsAdapter (private val chats: ArrayList<ChatDisplay>
) : RecyclerView.Adapter<ChatsViewHolder>() {


    var inter: IChatClickListener? = null



    fun updateChats(
        newChats: ArrayList<ChatDisplay>
    ){

        chats.clear()

        chats.addAll(newChats)

        notifyDataSetChanged()

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatsViewHolder {


        val binding =
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ChatsViewHolder(binding)
    }



    override fun onBindViewHolder(
        holder: ChatsViewHolder,
        position: Int
    ) {
        holder.bind(chats[position],inter)
        holder.itemView.setOnClickListener {
            inter?.onChatClick(
                chats[position]
            )
        }
    }
    override fun getItemCount(): Int {
        return chats.size
    }
}