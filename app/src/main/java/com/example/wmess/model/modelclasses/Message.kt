package com.example.wmess.model.modelclasses

import java.time.*
import java.util.*

data class Message(
    val userFrom: UUID?,
    val userTo: UUID?,
    val messageType: MessageType,
    val content: String?,
    val fileHandle: String?,
    val sentDate: Instant,
    val isRead: Boolean = false
)

enum class MessageType {
    TEXT, FILE
}