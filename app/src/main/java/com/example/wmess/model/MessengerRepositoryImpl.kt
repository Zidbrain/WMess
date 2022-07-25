package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.QueryResult.*
import com.example.wmess.di.*
import com.example.wmess.model.api.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import java.util.*
import java.util.concurrent.TimeUnit.*

class MessengerRepositoryImpl(
    private val messengerApi: MessengerApi,
    private val client: OkHttpClient,
    private val webSocketListener: MessengerWebSocketListener,
    accessToken: String
) :
    MessengerRepository(accessToken) {
    private lateinit var users: Map<UUID, User>
    private var currentUser: User? = null

    private lateinit var webSocket: WebSocket

    override suspend fun getUserById(id: UUID): QueryResult<User?> {
        if (!this::users.isInitialized)
            getUsers().onFailure { return it }
        return resultOf(users[id])
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

    override val notifications: QueryResult<Flow<Pair<User, Message>>>
        get() {
            if (this::webSocket.isInitialized && webSocketListener.isListening)
                webSocket.close(1001, null)
            webSocket = client.newBuilder()
                .pingInterval(20, SECONDS)
                .build()
                .newWebSocket(
                    Request.Builder()
                        .url("${BASE_URL}messenger/connect?accessToken=$accessToken")
                        .build(),
                    webSocketListener
                )
            return resultOf(webSocketListener.socketChannel.receiveAsFlow()
                .map {
                    users.getOrElse(it.userFrom!!) {
                        (getUsers() as Success<List<User>>).data.first { user -> user.id == it.userFrom }
                    } to it
                }.onCompletion {
                    if (it == null)
                        webSocket.close(1001, null)
                    else
                        webSocket.close(1002, null)
                })
        }
}