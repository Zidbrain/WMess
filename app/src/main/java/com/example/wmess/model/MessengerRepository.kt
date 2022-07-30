package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.flow.*
import java.util.*

abstract class MessengerRepository(protected val accessToken: String) {
    abstract suspend fun getUserById(id: UUID): QueryResult<User>

    abstract suspend fun getCurrentUser(): QueryResult<User>
    abstract suspend fun getUsers(): QueryResult<List<User>>
    abstract suspend fun patchUser(user: User): QueryResult<Unit>
    abstract suspend fun getHistoryByUsers(): QueryResult<Map<User, List<Message>>>
    abstract suspend fun getHistoryWith(uuid: UUID): QueryResult<List<Message>>

    abstract fun reconnect()
    abstract val notifications: QueryResult<Flow<Pair<User, Message>>>
    abstract fun send(message: Message): QueryResult<Unit>
}