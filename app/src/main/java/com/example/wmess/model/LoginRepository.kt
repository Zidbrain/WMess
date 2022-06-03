package com.example.wmess.model

import com.example.wmess.model.modelclasses.*

interface LoginRepository {
    suspend fun register(registerInfo: RegisterInfo): RegisterResult
    suspend fun login(loginInfo: LoginInfo): LoginResult

    suspend fun getCachedLoginInfo(): LoginInfo?
}