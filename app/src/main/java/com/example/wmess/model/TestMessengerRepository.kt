package com.example.wmess.model

import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.*

@Singleton
object TestMessengerRepositoryFactory : MessengerRepositoryFactory<TestMessengerRepository> {
    override fun provide(accessToken: String): TestMessengerRepository =
        TestMessengerRepository(accessToken)
}

class TestMessengerRepository(accessToken: String) : MessengerRepository(accessToken) {
    private lateinit var users: MutableList<User>

    private suspend fun cachedUsers(): List<User> {
        if (!::users.isInitialized) {
            reloadUsers()
        }
        return users
    }

    override suspend fun reloadUsers(): List<User> {
        delay(1000)
        users = mutableListOf(
            User(UUID.randomUUID(), "Nickname1", "+79999999999", "Status1"),
            User(UUID.randomUUID(), "Nickname2", "+79999999992", "Status2")
        )
        return users
    }

    override suspend fun getCurrentUser(): User =
        cachedUsers()[0]

    override suspend fun getUsers() = cachedUsers()

    override suspend fun patchUser(user: User) {
        users[0] = user
    }
}