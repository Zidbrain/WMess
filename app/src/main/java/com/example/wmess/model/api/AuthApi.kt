package com.example.wmess.model.api

import com.example.wmess.model.modelclasses.*
import retrofit2.*
import retrofit2.http.*

interface AuthApi {
    /**
     * Login with provided login info
     * @return Access token
     */
    @POST("auth")
    suspend fun login(@Body loginInfo: LoginInfo): Response<AuthApiLoginResponse>

    @POST("user/register")
    suspend fun register(@Body registerInfo: RegisterInfo): Response<User>
}