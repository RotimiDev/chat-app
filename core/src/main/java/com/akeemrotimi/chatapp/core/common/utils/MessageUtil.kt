package com.akeemrotimi.chatapp.core.common.utils

fun getLastMessagePreview(
    message: String?,
    type: String,
): String =
    when (type) {
        "audio" -> "Audio"
        "video" -> "Video"
        "image" -> "Photo"
        "emoji" -> "Emoji"
        else -> message ?: ""
    }
