package com.akeemrotimi.chatapp.feature.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.akeemrotimi.chatapp.core.common.formatTime
import com.akeemrotimi.chatapp.core.common.utils.getLastMessagePreview
import com.akeemrotimi.chatapp.core.data.model.ChatWithUser
import com.akeemrotimi.chatapp.feature.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ChatHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatHomeViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit,
    onProfileClick: () -> Unit,
) {
    var refreshing by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("chats") }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF141414),
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Box(
                            modifier =
                                Modifier
                                    .size(32.dp)
                                    .background(color = Color(0xFFAA9EE8), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "SF",
                                color = Color(0xFFF9F8FD),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "chats",
                    onClick = { selectedTab = "chats" },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chats),
                            contentDescription = "Chats",
                        )
                    },
                    label = { Text("Chats") },
                )
                NavigationBarItem(
                    selected = selectedTab == "calls",
                    onClick = { selectedTab = "calls" },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_call),
                            contentDescription = "Calls",
                        )
                    },
                    label = { Text("Calls") },
                )
                NavigationBarItem(
                    selected = selectedTab == "users",
                    onClick = { selectedTab = "users" },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "Users",
                        )
                    },
                    label = { Text("Users") },
                )
                NavigationBarItem(
                    selected = selectedTab == "groups",
                    onClick = { selectedTab = "groups" },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = "Groups",
                        )
                    },
                    label = { Text("Groups") },
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "chats" -> {
                    when (uiState) {
                        is HomeUiState.Loading -> LoadingContent()
                        is HomeUiState.Success -> {
                            val currentState = uiState as HomeUiState.Success
                            val pullRefreshState =
                                rememberPullRefreshState(
                                    refreshing = refreshing,
                                    onRefresh = {
                                        refreshing = true
                                        viewModel.loadChats()
                                        refreshing = false
                                    },
                                )
                            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                                ChatList(
                                    chats = currentState.chats,
                                    onChatClick = onChatClick,
                                )
                                PullRefreshIndicator(
                                    refreshing = refreshing,
                                    state = pullRefreshState,
                                    modifier = Modifier.align(Alignment.TopCenter),
                                )
                            }
                        }

                        is HomeUiState.Empty -> {
                            val currentState = uiState as HomeUiState.Empty
                            EmptyState(
                                title = currentState.title,
                                message = currentState.message,
                            )
                        }

                        is HomeUiState.Error -> {
                            val currentState = uiState as HomeUiState.Error
                            ErrorState(message = currentState.message)
                        }
                    }
                }

                "users" -> {
                    UsersScreen(
                        onUserClick = onChatClick,
                    )
                }

                "calls" -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Calls coming soon")
                    }
                }

                "groups" -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Groups coming soon")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(8) {
            ChatItemSkeleton()
        }
    }
}

@Composable
private fun ChatItemSkeleton() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
            )
        }

        Box(
            modifier =
                Modifier
                    .width(40.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        )
    }
}

@Composable
private fun ChatList(
    chats: List<ChatWithUser>,
    onChatClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(chats) { chatWithUser ->
            ChatItem(
                chatWithUser = chatWithUser,
                onClick = { onChatClick(chatWithUser.chat.id) },
            )
        }
    }
}

@Composable
private fun ChatItem(
    chatWithUser: ChatWithUser,
    onClick: () -> Unit,
) {
    val chat = chatWithUser.chat
    val user = chatWithUser.otherUser

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(56.dp),
        ) {
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "${user.displayName} profile",
                    modifier =
                        Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = user.displayName.take(2).uppercase(),
                        color = Color(0xFF757575),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
            }

            if (user.isOnline) {
                Box(
                    modifier =
                        Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .border(2.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd),
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = user.displayName,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        ),
                    color = Color(0xFF141414),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = formatTime(chat.lastMessageTime),
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                        ),
                    color = Color(0xFF9E9E9E),
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = getLastMessagePreview(chat.lastMessage, chat.lastMessageType),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                        ),
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (chat.unreadCount > 0) {
                    Box(
                        modifier =
                            Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6C5CE7)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    }
                }
            }
        }
    }

    Divider(
        color = Color(0xFFEEEEEE),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 88.dp, end = 16.dp),
    )
}

@Composable
private fun EmptyState(
    message: String,
    title: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_empty_illustration),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chat_error),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
    }
}
