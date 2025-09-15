package com.akeemrotimi.chatapp.feature.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is UsersUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UsersUiState.Error -> {
            val message = (state as UsersUiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = message, color = Color.Red)
            }
        }

        is UsersUiState.Success -> {
            val users = (state as UsersUiState.Success).users
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users) { user ->
                    ListItem(
                        headlineContent = { Text(user.displayName) },
                        supportingContent = { Text(user.email) },
                        leadingContent = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.startChatWithUser(user.id) { chatId ->
                                        onUserClick(chatId)
                                    }
                                },
                    )
                    Divider()
                }
            }
        }
    }
}
