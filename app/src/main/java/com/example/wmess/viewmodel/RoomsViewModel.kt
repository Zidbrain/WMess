package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import coil.*
import com.example.wmess.*
import com.example.wmess.R
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
        fun addMessage(message: Message): MessageInfo =
            MessageInfo(message, unreadAmount + 1)

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
        uiState = Error(R.string.error_message, it.error)
    }

    private var connectionJob: Job? = null

    fun connect() {
        if (connectionJob != null)
            connectionJob!!.cancel(null)

        connectionJob = repository.notifications.getOrElse { setError(it); return@connect }
            .onEach { (user, message) ->
                _rooms.replaceOrPut(user, MessageInfo(message, 1)) { it.addMessage(message) }
            }
            .onCompletion { connectionError = it }
            .cancellable()
            .launchIn(viewModelScope)
    }

    fun loadRooms() {
        uiState = Loading
        viewModelScope.launch(Dispatchers.IO) {
            currentUser =
                repository.getCurrentUser().getOrElse { setError(it); return@launch }

            val history = repository.getHistoryByUsers().getOrElse { setError(it); return@launch }
                .asIterable()
            _rooms.clear()
            _rooms.putAll(history.associate { (user, messages) ->
                user to MessageInfo(messages.first(), 0)
            })

            uiState = Loaded
        }
    }
}