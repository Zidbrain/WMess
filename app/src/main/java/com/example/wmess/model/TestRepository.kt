package com.example.wmess.model

import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Singleton

@Singleton
class TestRepository : WMessRepository {
    private val users = mutableMapOf(
        Pair(
            "user",
            Pair(User(UUID.randomUUID(), "user", null, null), "user")
        )
    )

    private val cache = LoginInfo("user", "user")

    override suspend fun getCachedLoginInfo(): LoginInfo {
        return cache
    }

    override suspend fun register(registerInfo: RegisterInfo): RegisterResult {
        delay(1000)

        if (users.containsKey(registerInfo.login))
            return RegisterResult.UserAlreadyExists

        val user = User(UUID.randomUUID(), registerInfo.username, null, null)
        users[registerInfo.login] = Pair(user, registerInfo.password)
        return RegisterResult.Success(user)
    }

    override suspend fun login(loginInfo: LoginInfo): LoginResult {
        delay(1000)

        if (users[loginInfo.login]?.second == loginInfo.password) {
            return LoginResult.Success("login: ${loginInfo.login}\npassword: ${loginInfo.password}")
        }
        return LoginResult.UserNotFound
    }
}