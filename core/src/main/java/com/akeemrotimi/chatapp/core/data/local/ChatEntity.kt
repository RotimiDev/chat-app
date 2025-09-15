package com.akeemrotimi.chatapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akeemrotimi.chatapp.core.data.model.Chat

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val participants: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int,
    val isGroup: Boolean,
    val groupName: String?,
)

fun ChatEntity.toDomain(): Chat =
    Chat(
        id = id,
        members = participants.split(","),
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isGroup = isGroup,
        groupName = groupName,
    )

fun Chat.toEntity(): ChatEntity =
    ChatEntity(
        id = id,
        participants = members.joinToString(","),
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isGroup = isGroup,
        groupName = groupName,
    )
