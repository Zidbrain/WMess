package com.example.wmess.model.modelclasses

import com.google.gson.*
import com.google.gson.annotations.*
import java.time.*
import java.util.*

data class Message(
    val userFrom: UUID?,
    val userTo: UUID?,
    val messageType: MessageType,
    val content: String?,
    @SerializedName("fileID") val fileHandle: UUID?,
    val dateSent: Instant,
    val isRead: Boolean = false
)

enum class MessageType {
    TEXT,
    FILE
}

enum class NotificationType {
    @SerializedName("MessageArray") MESSAGE_ARRAY,
    @SerializedName("Message") MESSAGE
}

data class Notification(val notificationType: NotificationType, val content: JsonElement)