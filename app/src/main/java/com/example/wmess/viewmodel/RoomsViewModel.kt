package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import coil.*
import com.example.wmess.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.collections.component1
import kotlin.collections.component2

class RoomsViewModel(
    private val repository: MessengerRepository,
    val imageLoader: ImageLoader
) : ViewModel() {

    data class MessageInfo(val lastMessage: Message, val unreadAmount: Int) {
        fun addMessage(message: Message, addUnread: Int = 1): MessageInfo =
            MessageInfo(message, unreadAmount + addUnread)

        fun readAll(): MessageInfo =
            MessageInfo(lastMessage, 0)
    }

    var uiState by mutableStateOf<UiState>(Initialized)
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    private val _rooms = mutableStateMapOf<User, MessageInfo>()
    var rooms: Map<User, MessageInfo> = _rooms

    var connectionError by mutableStateOf<Throwable?>(null)
        private set

    /**
     * Temporary
     */
    fun readMessages(user: User) {
        _rooms.replace(user) { it.readAll() }
    }

    private fun setError(it: QueryResult.Error) {
        uiState = Error(it)
    }

    private var connectionJob: Job? = null

    fun reconnect() {
        repository.reconnect()
        connect()
    }

    fun connect() {
        if (connectionJob != null)
            connectionJob!!.cancel(null)

        connectionJob = repository.notifications.getOrElse { setError(it); return }
            .onEach { (user, message) ->
                _rooms.replaceOrPut(
                    user,
                    MessageInfo(message, if (currentUser!!.id == message.userFrom) 0 else 1)
                ) { it.addMessage(message, if (currentUser!!.id == message.userFrom) 0 else 1) }
            }
            .onCompletion {
                if (it !is CancellationException) connectionError = it
                uiState = Initialized
            }
            .cancellable()
            .launchIn(viewModelScope)
    }

    fun loadRooms() {
        uiState = Loading

        viewModelScope.launch(Dispatchers.IO) {
            currentUser =
                repository.getCurrentUser().getOrElse { setError(it); return@launch }

            val history = repository.getHistoryByUsers().getOrElse { setError(it); return@launch }
                .entries
            _rooms.clear()
            _rooms.putAll(history.associate { (user, messages) ->
                user to MessageInfo(messages.maxBy { it.dateSent }, 0)
            })

            uiState = Loaded
        }
    }
}