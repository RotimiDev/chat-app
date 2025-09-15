package com.akeemrotimi.chatapp.core.data.model

data class Chat(
    val id: String = "",
    val members: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val lastMessageType: MessageType = MessageType.TEXT,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val groupName: String? = null,
)
