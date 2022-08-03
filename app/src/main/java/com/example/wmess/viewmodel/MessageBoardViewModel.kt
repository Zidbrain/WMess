package com.example.wmess.viewmodel

import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.model.modelclasses.MessageType.*
import com.example.wmess.viewmodel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.time.*
import java.util.*

class MessageBoardViewModel(
    private val repository: MessengerRepository,
    private val currentUser: UUID,
    private val withUser: UUID
) : ViewModel() {

    private var connectionJob: Job? = null

    private fun connect() {
        connectionJob = repository.notifications.getOrElse { uiState = Error(it); return }
            .onEach { _history.add(it.second) }
            .onCompletion {
                connectionJob = null
                if (it !is CancellationException) {
                    uiState = Error(it?.message.orEmpty())
                    connect()
                }
            }
            .cancellable()
            .launchIn(viewModelScope)
    }

    private val _history = mutableStateListOf<Message>()
    val history: List<Message> by lazy {
        if (connectionJob == null)
            connect()

        viewModelScope.launch {
            repository.getHistoryWith(withUser).switch(
                onSuccess = { _history.addAll(it) },
                onFailure = { uiState = Error(it) }
            )
        }
        _history
    }

    var textInput by mutableStateOf("")

    var uiState by mutableStateOf<UiState>(Loaded)
        private set

    var filename by mutableStateOf("")
        private set
    var fileUploadProgress: Float? by mutableStateOf(null)
        private set

    private var fileUUID: UUID? = null
    private var uploadJob: Job? = null

    fun uploadFile(inputStream: InputStream, fileName: String) {
        fileUploadProgress = 0f
        filename = fileName

        uploadJob = viewModelScope.launch(Dispatchers.IO) {
            inputStream.use { stream ->
                fileUUID = repository.uploadFile(stream, fileName) { progress ->
                    fileUploadProgress = progress
                }
                    .getOrElse { throw it.cause }
            }
        }
    }

    fun send() {
        val isFileUpload = fileUploadProgress != null
        fileUploadProgress = null

        repository.send(
            Message(
                userFrom = currentUser,
                userTo = withUser,
                messageType = if (isFileUpload) FILE else TEXT,
                content = if (isFileUpload) null else textInput,
                fileHandle = if (isFileUpload) fileUUID else null,
                dateSent = Instant.now(),
                isRead = true
            )
        )
        textInput = ""
    }

    fun cancelUpload() {
        uploadJob!!.cancel()
        fileUploadProgress = null
    }

    private var _fileInfo = mutableStateMapOf<Message, FileInfo>()
    val fileInfo: Map<Message, FileInfo> = _fileInfo

    suspend fun getFileInfo(message: Message) {
        withContext(Dispatchers.IO) {
            _fileInfo[message] = repository.getFileInfo(message.fileHandle!!).getOrThrow()
        }
    }

    private val downloadJobs = mutableMapOf<Message, Job>()
    private var _fileDownloadProgress = mutableStateMapOf<Message, Float>()
    val fileDownloadProgress: Map<Message, Float> = _fileDownloadProgress

    fun downloadFile(message: Message, stream: OutputStream) {
        _fileDownloadProgress[message] = 0f

        downloadJobs[message] = viewModelScope.launch(Dispatchers.IO) {
            stream.use {
                repository.getFile(message.fileHandle!!, it) { progress ->
                    _fileDownloadProgress[message] = progress
                    Log.i(null, progress.toString())
                }.getOrElse { error -> throw error.cause }
            }
        }
    }

    fun cancelDownloadFile(message: Message) {
        downloadJobs[message]?.cancel()
        _fileDownloadProgress.remove(message)
    }
}