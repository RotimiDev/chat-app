package com.akeemrotimi.chatapp.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akeemrotimi.chatapp.core.data.ChatRepository
import com.akeemrotimi.chatapp.core.data.UserRepository
import com.akeemrotimi.chatapp.core.data.model.Message
import com.akeemrotimi.chatapp.core.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
        val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

        private var currentChatId: String? = null
        private var currentUserId: String? = null

        fun loadChat(chatId: String) {
            viewModelScope.launch {
                currentUserId = userRepository.getCurrentUserId()
                currentChatId = chatId

                val chat = chatRepository.getChat(chatId)
                chatRepository.syncPendingMessages(chatId)

                if (chat == null || currentUserId == null) {
                    _uiState.value = ChatUiState.Error("Chat not found or user not logged in")
                    return@launch
                }

                val otherUserId = chat.userIds.firstOrNull { it != currentUserId }
                val otherUser = otherUserId?.let { userRepository.getUser(it) }

                combine(
                    chatRepository.getMessagesFlow(chatId),
                    userRepository.getUsersFlow(chat.userIds),
                ) { messages, users ->
                    ChatUiState.Success(
                        messages = messages,
                        otherUser = otherUser,
                        currentUserId = currentUserId!!,
                        participants = users,
                    )
                }.collect { _uiState.value = it }
            }
        }

        fun sendMessage(message: String) {
            val chatId = currentChatId ?: return
            val senderId = currentUserId ?: return
            if (message.isBlank()) return

            viewModelScope.launch {
                val messageObj =
                    Message(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        senderId = senderId,
                        content = message.trim(),
                        timestamp = System.currentTimeMillis(),
                        isSynced = false,
                    )
                chatRepository.sendMessage(chatId, messageObj)
            }
        }
    }

sealed class ChatUiState {
    data object Loading : ChatUiState()

    data class Success(
        val messages: List<Message>,
        val otherUser: User?,
        val currentUserId: String,
        val participants: List<User>,
        val isTyping: Boolean = false,
    ) : ChatUiState()

    data class Error(
        val message: String,
    ) : ChatUiState()
}
