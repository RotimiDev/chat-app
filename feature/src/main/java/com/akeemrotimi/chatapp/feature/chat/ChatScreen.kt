package com.akeemrotimi.chatapp.feature.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.akeemrotimi.chatapp.core.common.formatTime
import com.akeemrotimi.chatapp.core.data.model.Message
import com.akeemrotimi.chatapp.core.data.model.User
import com.akeemrotimi.chatapp.core.ui.StatusBarTheme
import com.akeemrotimi.chatapp.feature.R

@Composable
fun ChatScreen(
    chatId: String,
    onSendMessage: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    StatusBarTheme(
        color = Color(0XFFFAFAFA),
        lightIcons = false
    )

    var messageText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ChatUiState.Loading -> {
                ChatTopBarSkeleton()
                LoadingChatContent()
            }

            is ChatUiState.Success -> {
                state.otherUser?.let {
                    ChatTopBar(
                        user = it,
                        isTyping = state.isTyping,
                        onBackClick = onBackClick,
                        onCallClick = onCallClick,
                        onVideoCallClick = onVideoCallClick,
                        onMoreClick = onMoreClick,
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (state.messages.isEmpty()) {
                        EmptyChatState()
                    } else {
                        MessagesList(
                            messages = state.messages,
                            currentUserId = state.currentUserId,
                        )
                    }
                }

                MessageInput(
                    messageText = messageText,
                    onMessageTextChange = { messageText = it },
                    onSendMessage = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                )
            }

            is ChatUiState.Error -> {
                ErrorState(message = state.message)
            }
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, color = Color.Red)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    user: User,
    isTyping: Boolean,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (user.profilePictureUrl != null) {
                    AsyncImage(
                        model = user.profilePictureUrl,
                        contentDescription = "${user.displayName} profile",
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = user.displayName.take(2).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text =
                            when {
                                isTyping -> "typing..."
                                user.isOnline -> "Online"
                                else -> "Last seen ${formatTime(user.lastSeen)}"
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (user.isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onVideoCallClick) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cam),
                    contentDescription = "Video call",
                )
            }
            IconButton(onClick = onCallClick) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calls),
                    contentDescription = "Call",
                )
            }
            IconButton(onClick = onMoreClick) {
                Image(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "More",
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBarSkeleton() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier =
                            Modifier
                                .width(60.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
    )
}

@Composable
private fun LoadingChatContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(10) { index ->
            MessageSkeleton(isFromCurrentUser = index % 3 == 0)
        }
    }
}

@Composable
private fun MessageSkeleton(isFromCurrentUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier =
                Modifier
                    .widthIn(min = 80.dp, max = 250.dp)
                    .height(40.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                            bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp,
                        ),
                    ).background(MaterialTheme.colorScheme.surfaceVariant),
        )
    }
}

@Composable
private fun MessagesList(
    messages: List<Message>,
    currentUserId: String,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        reverseLayout = true,
    ) {
        items(messages.reversed()) { message ->
            MessageItem(
                message = message,
                isFromCurrentUser = message.senderId == currentUserId,
            )
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    isFromCurrentUser: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
    ) {
        Card(
            modifier = Modifier.widthIn(min = 80.dp, max = 280.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (isFromCurrentUser) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                ),
            shape =
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp,
                ),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (isFromCurrentUser) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color =
                            if (isFromCurrentUser) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            },
                    )

                    if (isFromCurrentUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_sent),
                            contentDescription = "Sent",
                            modifier = Modifier.size(14.dp),
                            colorFilter =
                                ColorFilter.tint(
                                    if (message.readBy.isNotEmpty()) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    },
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyChatState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Type your message...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MessageInput(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            IconButton(onClick = { }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add attachment",
                )
            }

            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your message...") },
                maxLines = 4,
                trailingIcon = {
                    Row {
                        IconButton(onClick = { }) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_voice_note),
                                contentDescription = "Voice message",
                            )
                        }
                        IconButton(onClick = { }) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_face),
                                contentDescription = "Emoji",
                            )
                        }
                        IconButton(onClick = { }) {
                            Image(
                                painterResource(id = R.drawable.ic_upload),
                                contentDescription = "Camera",
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = onSendMessage,
                modifier = Modifier.size(48.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_send_button),
                    contentDescription = "Send message",
                )
            }
        }
    }
}
