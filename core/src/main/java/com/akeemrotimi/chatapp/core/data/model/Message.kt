package com.akeemrotimi.chatapp.core.data.model

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.TEXT,
    val readBy: List<String> = emptyList(),
    val deliveredTo: List<String> = emptyList(),
    val isSynced: Boolean = true
)

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT
}
