package com.example.electrohub.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electrohub.adapters.MessagesAdapter
import com.example.electrohub.databinding.FragmentChatBinding
import com.example.electrohub.models.Messages
import com.example.electrohub.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var _binding: FragmentChatBinding

    private lateinit var _messagesAdapter: MessagesAdapter

    private val _messagesList = ArrayList<Messages>()

    private val _viewModel: ChatViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentChatBinding.inflate(
                inflater,
                container,
                false
            )

        ViewCompat.setOnApplyWindowInsetsListener(
            _binding.root
        ) { _, insets ->

            val keyboardHeight =
                insets.getInsets(
                    WindowInsetsCompat.Type.ime()
                ).bottom

            // Move input above keyboard
            _binding.chatInputLayout.translationY =
                -keyboardHeight.toFloat()

            // Move RecyclerView's bottom up with the input
            val layoutParams =
                _binding.rvMessages.layoutParams
                        as ViewGroup.MarginLayoutParams

            layoutParams.bottomMargin = keyboardHeight

            _binding.rvMessages.layoutParams =
                layoutParams

            // Keep the latest message visible
            if (_messagesList.isNotEmpty()) {

                _binding.rvMessages.post {

                    _binding.rvMessages.scrollToPosition(
                        _messagesList.size - 1
                    )
                }
            }

            insets
        }

        return _binding.root
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupRecyclerView()
        setViews()
        setObservers()
        getChatId()
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private fun setupRecyclerView() {

        val currentUserId = _viewModel.getCurrentUserId()

        _messagesAdapter =
            MessagesAdapter(
                _messagesList,
                currentUserId
            )

        _binding.rvMessages.apply {

            adapter = _messagesAdapter

            layoutManager =
                LinearLayoutManager(
                    requireContext()
                ).apply {

                    orientation =
                        LinearLayoutManager.VERTICAL
                }
        }
    }


    // =========================================================
    // GET CHAT ID
    // =========================================================

    private fun getChatId() {

        val chatId =
            arguments?.getString("chatId")

        if (chatId != null) {

            _viewModel.setChatId(chatId)

            _viewModel.loadChatInfo(chatId)
        }
    }


    // =========================================================
    // VIEWS
    // =========================================================

    private fun setViews() {

        _binding.btnBack.setOnClickListener {

            findNavController().navigateUp()
        }


        _binding.btnSend.setOnClickListener {

            val text =
                _binding.etxtMessage
                    .text
                    .toString()
                    .trim()

            if (text.isNotEmpty()) {

                _viewModel.sendMessage(text)

                _binding.etxtMessage
                    .text
                    .clear()
            }
        }
    }


    // =========================================================
    // STATE FLOW OBSERVERS
    // =========================================================

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {

                    _viewModel.messages
                        .collect { messages ->

                            _messagesAdapter
                                .updateMessages(
                                    ArrayList(messages)
                                )

                            if (messages.isNotEmpty()) {

                                _binding.rvMessages.post {

                                    _binding.rvMessages.scrollToPosition(
                                        messages.size - 1
                                    )
                                }
                            }
                        }
                }


                launch {

                    _viewModel.chatInfo
                        .collect { chat ->

                            chat?.let {

                                _binding.txtVProductName
                                    .text =
                                    it.productName
                            }
                        }
                }


                launch {

                    _viewModel.sellerName.collect { name ->
                            _binding.txtVSellerName.text = name
                        }
                }
            }
        }
    }


    override fun onDestroyView() {

        super.onDestroyView()
    }
}