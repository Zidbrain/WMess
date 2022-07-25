package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.modelclasses.*
import com.google.gson.*
import kotlinx.coroutines.channels.*
import okhttp3.*
import okio.*

class MessengerWebSocketListener(private val gson: Gson) :
    WebSocketListener() {
    val onClosedEvent: Event<String> = Event()
    val onFailureEvent: Event<Throwable> = Event()

    val socketChannel: Channel<Message> = Channel(Channel.UNLIMITED)

    var isListening: Boolean = false
        private set

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        socketChannel.close(null)
        isListening = false
        onClosedEvent(reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(code, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketChannel.close(t)
        isListening = false
        onFailureEvent(t)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val actualText = text.substringBefore('\u0000')
        try {
            socketChannel.trySend(gson.fromJson(actualText))
        } catch (ex: JsonParseException) {
            val res = gson.fromJson<List<Message>>(actualText)
            res.forEach {
                socketChannel.trySend(it)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        webSocket.close(1003, "Unsupported data")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        isListening = true
    }
}