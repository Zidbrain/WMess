package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.modelclasses.*
import com.google.gson.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.*

class MessengerWebSocketListener(private val gson: Gson) :
    WebSocketListener() {
    val onClosedEvent: Event<String> = Event()
    val onFailureEvent: Event<Throwable> = Event()

    private val _socketFlow =
        MutableSharedFlow<Message>(extraBufferCapacity = Int.MAX_VALUE)
    val socketFlow = _socketFlow.asSharedFlow()

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onClosedEvent(reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(code, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        onFailureEvent(t)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val actualText = text.substringBefore('\u0000')
        try {
            _socketFlow.tryEmit(gson.fromJson(actualText))
        } catch (ex: JsonParseException) {
            val res = gson.fromJson<List<Message>>(actualText)
            res.forEach {
                _socketFlow.tryEmit(it)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        webSocket.close(1003, "Unsupported data")
    }

    fun sendToFlow(message: Message) =
        _socketFlow.tryEmit(message)
}