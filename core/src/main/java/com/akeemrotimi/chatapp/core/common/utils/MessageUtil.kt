package com.akeemrotimi.chatapp.core.common.utils

import com.akeemrotimi.chatapp.core.data.model.MessageType

fun getLastMessagePreview(
    lastMessage: String,
    messageType: MessageType,
): String =
    when (messageType) {
        MessageType.TEXT -> {
            if (lastMessage.contains("@")) {
                val parts = lastMessage.split("@")
                if (parts.size > 1) {
                    "${parts[0].trim()} @${parts[1].split(" ").first()}"
                } else {
                    lastMessage
                }
            } else {
                lastMessage
            }
        }

        MessageType.IMAGE -> "Photo"
        MessageType.VIDEO -> "Video"
        MessageType.AUDIO -> "Audio"
        MessageType.DOCUMENT -> "Document"
    }
