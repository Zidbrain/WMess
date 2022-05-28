package com.example.wmess.model

import com.example.wmess.model.modelclasses.LoginInfo
import com.example.wmess.model.modelclasses.LoginResult
import com.example.wmess.model.modelclasses.RegisterInfo
import com.example.wmess.model.modelclasses.RegisterResult

interface WMessRepository {
    suspend fun register(registerInfo: RegisterInfo): RegisterResult
    suspend fun login(loginInfo: LoginInfo): LoginResult

    suspend fun getCachedLoginInfo(): LoginInfo?
}