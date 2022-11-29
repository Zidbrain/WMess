package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.di.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.model.modelclasses.NotificationType.*
import com.google.gson.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import java.util.concurrent.TimeUnit.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

class MessagingService(
    private val client: OkHttpClient,
    private val accessTokenHolder: AccessTokenHolder,
    private val gson: Gson
) {
    private val _notificationFlow = MutableSharedFlow<Message>(extraBufferCapacity = Int.MAX_VALUE)
    val notificationFlow by lazy {
        open()
        _notificationFlow.asSharedFlow()
    }

    private var webSocket: WebSocket? = null

    private inner class SocketListener : WebSocketListener() {

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(code, null)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            open()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val actualText = text.substringBefore('\u0000')
            val notification = gson.fromJson<Notification>(actualText)

            when (notification.notificationType) {
                MESSAGE_ARRAY -> {
                    for (message in gson.fromJson<Collection<Message>>(
                        notification.content,
                        typeOf<Collection<Message>>().javaType
                    )) {
                        _notificationFlow.tryEmit(message)
                    }
                }
                MESSAGE -> _notificationFlow.tryEmit(
                    gson.fromJson(
                        notification.content,
                        Message::class.java
                    )
                )
            }
        }
    }

    fun reopen() {
        webSocket?.close(1001, null)
        open()
    }

    private fun open() {
        webSocket = client.newBuilder()
            .pingInterval(20, SECONDS)
            .build()
            .newWebSocket(
                Request.Builder()
                    .url("${BASE_URL}messenger/connect?accessToken=${accessTokenHolder.accessToken}")
                    .build(),
                SocketListener()
            )
    }
}