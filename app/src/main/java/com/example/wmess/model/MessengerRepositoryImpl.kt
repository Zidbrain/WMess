package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.QueryResult.*
import com.example.wmess.di.*
import com.example.wmess.model.api.*
import com.example.wmess.model.modelclasses.*
import com.google.gson.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import java.util.*
import java.util.concurrent.TimeUnit.*

class MessengerRepositoryImpl(
    private val messengerApi: MessengerApi,
    private val client: OkHttpClient,
    private val webSocketListener: MessengerWebSocketListener,
    private val gson: Gson,
    accessToken: String
) :
    MessengerRepository(accessToken) {
    private var users: Map<UUID, User> = mapOf()
    private var currentUser: User? = null

    private var webSocket: WebSocket? = null

    override suspend fun getUserById(id: UUID): QueryResult<User> {
        val ret = users.getOrElse(id) {
            getUsers().onFailure { return it }
            users[id]
        }
        return if (ret != null) resultOf(ret) else Error("User not found")
    }

    override suspend fun getCurrentUser(): QueryResult<User> =
        if (currentUser == null)
            safeCall { messengerApi.getCurrentUser() }.onSuccess { currentUser = it }
        else resultOf(currentUser!!)

    override suspend fun getUsers(): QueryResult<List<User>> =
        safeCall { messengerApi.getAllUsers() }.onSuccess {
            users = it.associateBy { user -> user.id }
        }

    override suspend fun patchUser(user: User): QueryResult<Unit> =
        safeCall { messengerApi.patchUser(user.toPatchUser()) }

    override suspend fun getHistoryByUsers(): QueryResult<Map<User, List<Message>>> {
        val user = getCurrentUser().getOrElse { return it }

        getUsers().onFailure { return it }

        return safeCall {
            messengerApi.getHistory()
        }.map { messages ->
            val map = mutableMapOf<User, MutableList<Message>>()
            messages.forEach {
                map.getOrPut(users[(if (it.userFrom == user.id) it.userTo else it.userFrom)!!]!!) {
                    mutableListOf()
                }.add(it)
            }
            map
        }
    }

    override suspend fun getHistoryWith(uuid: UUID): QueryResult<List<Message>> =
        safeCall { messengerApi.getHistoryWith(uuid) }

    private fun createSocket(): WebSocket =
        client.newBuilder()
            .pingInterval(20, SECONDS)
            .build()
            .newWebSocket(
                Request.Builder()
                    .url("${BASE_URL}messenger/connect?accessToken=$accessToken")
                    .build(),
                webSocketListener
            )

    private fun openWebSocket(): WebSocket {
        if (webSocket != null)
            return webSocket!!

        webSocket = createSocket()
        return webSocket!!
    }

    override fun reconnect() {
        if (webSocket != null)
            webSocket!!.close(1001, null)
        webSocket = createSocket()
    }

    override val notifications: QueryResult<Flow<Pair<User, Message>>>
        get() {
            openWebSocket()
            return resultOf(webSocketListener.socketFlow
                .map {
                    val checkIndex = if (it.userFrom!! == currentUser!!.id) it.userTo!! else it.userFrom
                    users.getOrElse(checkIndex) {
                        getUsers()
                        users[checkIndex]!!
                    } to it
                }.onCompletion {
                    if (it !is CancellationException) {
                        webSocket!!.close(1001, null)
                        webSocket = null
                    }
                }
            )
        }

    override fun send(message: Message): QueryResult<Unit> {
        return if (openWebSocket().send(gson.toJson(message))) {
            webSocketListener.sendToFlow(message)
            resultOf(Unit)
        } else Error("Error sending the message")
    }
}