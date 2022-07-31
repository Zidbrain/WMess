package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.api.*
import com.example.wmess.model.modelclasses.*

class AccessTokenHolder(
    private val authApi: AuthApi,
    private val loginInfo: LoginInfo,
    initialToken: String
) {
    var accessToken: String = initialToken
        private set

    suspend fun retrieve(): QueryResult<String> =
        safeCall {
            authApi.login(loginInfo)
        }
            .map { it.accessToken }
            .onSuccess { accessToken = it }

    companion object {
        suspend fun tryCreate(
            authApi: AuthApi,
            loginInfo: LoginInfo
        ): QueryResult<AccessTokenHolder> =
            safeCall {
                authApi.login(loginInfo)
            }.map { AccessTokenHolder(authApi, loginInfo, it.accessToken) }
    }
}