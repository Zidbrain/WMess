package com.example.wmess.model

import com.example.wmess.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.network.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.util.*

interface MessengerRepository {
    suspend fun getUserById(id: UUID): QueryResult<User>

    suspend fun getCurrentUser(): QueryResult<User>
    suspend fun getUsers(): QueryResult<List<User>>
    suspend fun patchUser(user: User): QueryResult<Unit>
    suspend fun getHistoryByUsers(): QueryResult<Map<User, List<Message>>>
    suspend fun getHistoryWith(uuid: UUID): QueryResult<List<Message>>

    fun reconnect()
    val notifications: QueryResult<Flow<Pair<User, Message>>>
    fun send(message: Message): QueryResult<Unit>

    suspend fun uploadFile(
        inputStream: InputStream,
        fileName: String,
        progressListener: ProgressListener? = null
    ): QueryResult<UUID>

    suspend fun getFileInfo(fileId: UUID): QueryResult<FileInfo>
    suspend fun getFile(
        fileId: UUID,
        fileStream: OutputStream,
        progressListener: ProgressListener? = null
    ): QueryResult<Unit>

    suspend fun changeAvatar(inputStream: InputStream): QueryResult<Unit>
}