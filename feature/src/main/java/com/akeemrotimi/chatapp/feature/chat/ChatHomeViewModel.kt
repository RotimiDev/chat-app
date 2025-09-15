package com.akeemrotimi.chatapp.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akeemrotimi.chatapp.core.data.AuthRepository
import com.akeemrotimi.chatapp.core.data.ChatRepository
import com.akeemrotimi.chatapp.core.data.UserRepository
import com.akeemrotimi.chatapp.core.data.model.ChatWithUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHomeViewModel
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init {
            loadChats()
        }

        fun loadChats() {
            viewModelScope.launch {
                try {
                    _uiState.value = HomeUiState.Loading

                    val currentUserId = getCurrentUserId()
                    if (currentUserId.isEmpty()) {
                        _uiState.value = HomeUiState.Error("User not logged in")
                        return@launch
                    }

                    chatRepository
                        .getChatsFlow(currentUserId)
                        .catch { exception ->
                            _uiState.value =
                                HomeUiState.Error("Failed to load chats: ${exception.message}")
                        }.collect { chats ->
                            if (chats.isEmpty()) {
                                _uiState.value = HomeUiState.Empty()
                            } else {
                                val otherUserIds =
                                    chats
                                        .flatMap { chat ->
                                            chat.members.filter { it != currentUserId }
                                        }.distinct()

                                val users = userRepository.getUsers(otherUserIds)
                                val userMap = users.associateBy { it.id }

                                val chatsWithUsers = chats.mapNotNull { chat ->
                                    val otherUserId = chat.members.firstOrNull { it != currentUserId }
                                    otherUserId?.let { userId ->
                                        userMap[userId]?.let { user -> ChatWithUser(chat, user) }
                                    }
                                }

                                if (chatsWithUsers.isEmpty()) {
                                    _uiState.value = HomeUiState.Empty()
                                } else {
                                    _uiState.value = HomeUiState.Success(chatsWithUsers)
                                }
                                _uiState.value = HomeUiState.Success(chatsWithUsers)
                            }
                        }
                } catch (e: Exception) {
                    _uiState.value = HomeUiState.Error("Failed to load chats: ${e.message}")
                }
            }
        }

        private fun getCurrentUserId(): String = authRepository.getCurrentUser()?.uid ?: ""
    }

sealed class HomeUiState {
    data object Loading : HomeUiState()

    data class Success(
        val chats: List<ChatWithUser>,
    ) : HomeUiState()

    data class Empty(
        val title: String = "No Conversations Yet",
        val message: String = "Start a new chat or invite others to join the conversation.",
    ) : HomeUiState()

    data class Error(
        val message: String,
    ) : HomeUiState()
}
