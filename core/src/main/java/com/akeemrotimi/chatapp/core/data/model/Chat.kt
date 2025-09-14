package com.akeemrotimi.chatapp.core.data.model

data class Chat(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val userIds: List<String> = emptyList(),
    val lastMessageType: MessageType = MessageType.TEXT,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val groupName: String? = null,
)
