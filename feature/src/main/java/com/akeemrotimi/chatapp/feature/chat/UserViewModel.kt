package com.akeemrotimi.chatapp.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akeemrotimi.chatapp.core.data.ChatRepository
import com.akeemrotimi.chatapp.core.data.UserRepository
import com.akeemrotimi.chatapp.core.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val chatRepository: ChatRepository
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
        val uiState: StateFlow<UsersUiState> = _uiState

        init {
            loadUsers()
        }

        private fun loadUsers() {
            viewModelScope.launch {
                val currentUserId = userRepository.getCurrentUserId()
                if (currentUserId == null) {
                    _uiState.value = UsersUiState.Error("User not logged in")
                    return@launch
                }

                userRepository.getAllUsersFlow().collect { users ->
                    _uiState.value =
                        UsersUiState.Success(
                            users.filter { it.id != currentUserId },
                        )
                }
            }
        }


    fun startChatWithUser(
        otherUserId: String,
        onChatCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId() ?: return@launch
            val chatId = chatRepository.createChatIfNotExists(currentUserId, otherUserId)
            onChatCreated(chatId)
        }
    }
    }

sealed class UsersUiState {
    data object Loading : UsersUiState()

    data class Success(
        val users: List<User>,
    ) : UsersUiState()

    data class Error(
        val message: String,
    ) : UsersUiState()
}
