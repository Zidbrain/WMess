package com.example.wmess.model

import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.flow.*
import java.util.*

abstract class MessengerRepository(protected val accessToken: String) {
    abstract fun getUserById(id: UUID): User?

    abstract suspend fun getCurrentUser(): User
    abstract suspend fun getUsers(): List<User>
    abstract suspend fun reloadUsers(): List<User>
    abstract suspend fun patchUser(user: User)
    abstract suspend fun getHistory(): List<Message>
    abstract suspend fun getHistoryByUsers(): Map<User, List<Message>>

    abstract val notifications: Flow<Pair<User, Message>>
}

fun interface MessengerRepositoryFactory <Repository: MessengerRepository> {
    fun provide(accessToken: String): Repository
}