package com.akeemrotimi.chatapp.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Date()
    val diff = now.time - date.time

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        else -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    }
}
