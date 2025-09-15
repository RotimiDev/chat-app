package com.akeemrotimi.chatapp.core.data.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profilePictureUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis(),
    val createdAt: Timestamp? = null,
)
