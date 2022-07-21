package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.api.*
import com.example.wmess.model.modelclasses.*

class LoginRepositoryImpl(private val authApi: AuthApi) : LoginRepository {

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

    override suspend fun login(loginInfo: LoginInfo): LoginResult =
        catchAll(LoginResult::Error) {
            val result = authApi.login(loginInfo)

            val body = result.body()
            if (body == null)
                if (result.code() == 400) LoginResult.UserNotFound
                else LoginResult.Error(result.message())
            else
                LoginResult.Success(body.accessToken)
        }

    override suspend fun getCachedLoginInfo(): LoginInfo {
        return LoginInfo("string", "string")
    }
}