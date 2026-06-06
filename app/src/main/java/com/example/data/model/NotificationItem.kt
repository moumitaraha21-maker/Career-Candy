package com.example.data.model

import java.util.UUID

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val type: String // "match" or "status" (or "system", "premium")
)
