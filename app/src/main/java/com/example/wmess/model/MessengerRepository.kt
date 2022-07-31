package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.flow.*
import java.util.*

interface MessengerRepository {
    suspend fun getUserById(id: UUID): QueryResult<User>

    suspend fun getCurrentUser(): QueryResult<User>
    suspend fun getUsers(): QueryResult<List<User>>
    suspend fun patchUser(user: User): QueryResult<Unit>
    suspend fun getHistoryByUsers(): QueryResult<Map<User, List<Message>>>
    suspend fun getHistoryWith(uuid: UUID): QueryResult<List<Message>>

    fun reconnect()
    val notifications: QueryResult<Flow<Pair<User, Message>>>
    fun send(message: Message): QueryResult<Unit>
}