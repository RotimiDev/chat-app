package com.akeemrotimi.chatapp.core.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatTime(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    val messageTime = Calendar.getInstance().apply { timeInMillis = timestamp }

    return when {
        calendar.get(Calendar.DATE) == messageTime.get(Calendar.DATE) -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        }

        calendar.get(Calendar.DATE) - messageTime.get(Calendar.DATE) == 1 -> "Yesterday"
        else -> SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(Date(timestamp))
    }
}
