package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.QueryResult.*
import com.example.wmess.model.api.*
import com.example.wmess.model.modelclasses.*
import org.koin.core.context.*
import org.koin.core.module.*
import org.koin.dsl.*

class LoginRepositoryImpl(private val authApi: AuthApi) : LoginRepository {

    private var accessTokenHolderModule: Module? = null

    override suspend fun register(registerInfo: RegisterInfo): RegisterResult =
        catchAll(RegisterResult::Error) {
            val result = authApi.register(registerInfo)

            val body = result.body()
            return if (body == null)
                if (result.code() == 409) RegisterResult.UserAlreadyExists
                else RegisterResult.Error(result.message())
            else
                RegisterResult.Success(body)
        }

    override suspend fun login(loginInfo: LoginInfo): LoginResult {
        val holder = AccessTokenHolder.tryCreate(authApi, loginInfo)
            .getOrElse {
                return if (it is ErrorCode && it.errorCode == 404) LoginResult.UserNotFound
                else LoginResult.Error(it.cause.message ?: "Error logging in")
            }

        if (accessTokenHolderModule != null)
            unloadKoinModules(accessTokenHolderModule!!)

        accessTokenHolderModule = module {
            single { holder }
        }
        loadKoinModules(accessTokenHolderModule!!)

        return LoginResult.Success
    }

    override suspend fun getCachedLoginInfo(): LoginInfo {
        return LoginInfo("string", "string")
    }
}