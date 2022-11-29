package com.example.wmess.model.api

import com.example.wmess.model.modelclasses.*
import com.example.wmess.network.*
import okhttp3.*
import retrofit2.Response
import retrofit2.http.*
import java.io.*
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

    @Multipart
    @POST("file")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<ApiUploadFile>

    @HEAD("file")
    suspend fun getFileInfo(@Query("fileHandle") fileHandle: UUID): Response<Void>

    @Streaming
    @GET("file")
    suspend fun getFile(
        @Query("fileHandle") fileHandle: UUID,
        @Tag stream: OutputStream,
        @Tag progressListener: ProgressListener? = null
    ): Response<ResponseBody>

    @Multipart
    @POST("images")
    suspend fun postAvatar(@Part image: MultipartBody.Part): Response<Unit>
}