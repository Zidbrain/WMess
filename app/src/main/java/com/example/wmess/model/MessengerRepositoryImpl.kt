package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.api.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.flow.*
import java.util.*

class MessengerRepositoryImpl(private val messengerApi: MessengerApi, accessToken: String) :
    MessengerRepository(accessToken) {
    private lateinit var users: Map<UUID, User>
    private var currentUser: User? = null

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
        get() = resultOf(flow {
        })
}