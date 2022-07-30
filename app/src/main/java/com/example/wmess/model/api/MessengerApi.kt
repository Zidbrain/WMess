package com.example.wmess.model.api

import com.example.wmess.model.modelclasses.*
import retrofit2.*
import retrofit2.http.*
import java.util.*

interface MessengerApi {
    @GET("user")
    suspend fun getCurrentUser(): Response<User>

    @GET("user/all")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("messenger/history")
    suspend fun getHistory(): Response<List<Message>>

    @PATCH("user")
    suspend fun patchUser(@Body apiPatchUser: ApiPatchUser): Response<Unit>

    @GET("messenger/history/{userId}")
    suspend fun getHistoryWith(@Path("userId") uuid: UUID): Response<List<Message>>
}