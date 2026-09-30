package com.example.electrohub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electrohub.R
import com.example.electrohub.adapters.ChatsAdapter
import com.example.electrohub.databinding.FragmentMyChatsBinding
import com.example.electrohub.interfaces.IChatClickListener
import com.example.electrohub.models.ChatDisplay
import com.example.electrohub.viewmodels.MyChatsViewModel
import kotlinx.coroutines.launch


class MyChatsFragment :
    Fragment(),
    IChatClickListener {


    private lateinit var _binding:
            FragmentMyChatsBinding


    private val _viewModel:
            MyChatsViewModel by viewModels()


    private lateinit var chatAdapter:
            ChatsAdapter


    // CREATE VIEW
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentMyChatsBinding.inflate(
                inflater,
                container,
                false
            )

        return _binding.root
    }


    // VIEW CREATED
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setViews()
        observeChats()

        _viewModel.loadMyChats()
    }

    // VIEWS
    private fun setViews() {

        _binding.rvChats.layoutManager =
            LinearLayoutManager(
                requireContext()
            )


        chatAdapter =
            ChatsAdapter(
                ArrayList()
            ).apply {

                inter =
                    this@MyChatsFragment
            }


        _binding.rvChats.adapter =
            chatAdapter


        _binding.btnBack.setOnClickListener {

            findNavController()
                .navigateUp()
        }
    }


    // OBSERVE CHATS

    private fun observeChats() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                _viewModel.myChats
                    .collect { chats ->

                        chatAdapter.updateChats(
                            ArrayList(chats)
                        )
                        if (chats.isEmpty()) {

                            _binding.llEmpty.visibility =
                                View.VISIBLE

                            _binding.rvChats.visibility =
                                View.GONE

                        } else {

                            _binding.llEmpty.visibility =
                                View.GONE

                            _binding.rvChats.visibility =
                                View.VISIBLE
                        }
                    }
            }
        }
    }



    // OPEN CHAT
    override fun onChatClick(
        chat: ChatDisplay
    ) {

        val bundle =
            Bundle().apply {

                putString(
                    "chatId",
                    chat.chatId
                )
            }


        findNavController().navigate(
            R.id.action_MyChatsFragment_to_ChatFragment,
            bundle
        )
    }


    // DELETE / HIDE CHAT
    override fun onDeleteChat(
        chat: ChatDisplay
    ) {

        _viewModel.hideChat(
            chat.chatId
        ) { success ->
            // MyChatsViewModel automatically
            // reloads the chats after success.
        }
    }


    // DESTROY VIEW
    override fun onDestroyView() {

        super.onDestroyView()
    }
}