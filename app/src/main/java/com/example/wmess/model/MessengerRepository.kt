package com.example.wmess.model

import com.example.wmess.model.modelclasses.*

abstract class MessengerRepository(protected val accessToken: String) {
    abstract suspend fun getCurrentUser(): User
    abstract suspend fun getUsers(): List<User>
    abstract suspend fun reloadUsers(): List<User>
    abstract suspend fun patchUser(user: User)
}

fun interface MessengerRepositoryFactory <Repository: MessengerRepository> {
    fun provide(accessToken: String): Repository
}