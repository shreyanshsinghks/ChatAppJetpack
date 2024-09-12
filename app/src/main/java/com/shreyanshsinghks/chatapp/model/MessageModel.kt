package com.shreyanshsinghks.chatapp.model

data class MessageModel(
    val id: String = "",
    val senderId: String = "",
    val message: String? = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderImage: String? = "",
    val imageUrl: String? = ""
)