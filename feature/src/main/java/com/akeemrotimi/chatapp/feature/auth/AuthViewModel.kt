package com.akeemrotimi.chatapp.feature.auth

import androidx.lifecycle.ViewModel
import com.akeemrotimi.chatapp.core.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        fun login(
            email: String,
            password: String,
        ) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.signIn(email, password) { success, error ->
                _uiState.value =
                    if (success) {
                        AuthUiState(isLoading = false, isLoggedIn = true)
                    } else {
                        AuthUiState(isLoading = false, errorMessage = error)
                    }
            }
        }

        fun signup(
            email: String,
            password: String,
            displayName: String,
        ) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.signUp(email, password, displayName) { success, error ->
                _uiState.value =
                    if (success) {
                        AuthUiState(isLoading = false, isLoggedIn = true)
                    } else {
                        AuthUiState(isLoading = false, errorMessage = error)
                    }
            }
        }

        fun resetPassword(email: String) {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.resetPassword(email) { success, error ->
                _uiState.value =
                    if (success) {
                        AuthUiState(isLoading = false)
                    } else {
                        AuthUiState(isLoading = false, errorMessage = error)
                    }
            }
        }

        fun clearError() {
            _uiState.value = _uiState.value.copy(errorMessage = null)
        }
    }

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
)
