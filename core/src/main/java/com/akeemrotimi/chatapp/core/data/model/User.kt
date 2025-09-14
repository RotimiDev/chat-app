package com.akeemrotimi.chatapp.core.data.model

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)
