package com.example.wmess.model.modelclasses

import com.google.gson.annotations.*
import java.time.*
import java.util.*

data class Message(
    val userFrom: UUID?,
    val userTo: UUID?,
    val messageType: MessageType,
    val content: String?,
    @SerializedName("fileID") val fileHandle: String?,
    val dateSent: Instant,
    val isRead: Boolean = false
)

enum class MessageType {
    @SerializedName("Text") TEXT,
    @SerializedName("File") FILE
}