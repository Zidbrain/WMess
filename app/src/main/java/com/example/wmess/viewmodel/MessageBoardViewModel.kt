package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.model.modelclasses.MessageType.*
import com.example.wmess.viewmodel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    fun send() {
        repository.send(Message(currentUser, withUser, TEXT, textInput, null, Instant.now(), true))
        textInput = ""
    }
}