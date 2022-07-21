package com.example.wmess.viewmodel

import androidx.lifecycle.*
import coil.*
import com.example.wmess.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class RoomsViewModel(
    private val repository: MessengerRepository,
    val imageLoader: ImageLoader
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(Initialized)
    val uiState: StateFlow<UiState> = _uiState

    private val _currentUser = MutableStateFlow(null as User?)
    val currentUser: StateFlow<User?> = _currentUser

    private val _rooms = MutableStateFlow(emptyMap<User, MutableStateFlow<Message>>())
    val rooms: StateFlow<Map<User, StateFlow<Message>>> = _rooms

    private val _unreadAmount = MutableStateFlow(emptyMap<User, MutableStateFlow<Int>>())
    val unreadAmount: StateFlow<Map<User, StateFlow<Int>>> = _unreadAmount

    /**
     * Temporary
     */
    fun readMessages(user: User) {
        _unreadAmount.value[user]?.update { 0 }
    }

    private fun setError(it: QueryResult.Error) {
        _uiState.value = Error(R.string.error_message, it.error)
    }

    fun loadRooms() {
        _uiState.value = Loading
        viewModelScope.launch(Dispatchers.IO) {
            _currentUser.value =
                repository.getCurrentUser().getOrElse { setError(it); return@launch }

            val history = repository.getHistoryByUsers().getOrElse { setError(it); return@launch }
                .asIterable()
            _rooms.value = history.associate { (user, messages) ->
                user to MutableStateFlow(messages.last())
            }
            _unreadAmount.value = history.associate { (user, messages) ->
                user to MutableStateFlow(messages.count { it.userTo == _currentUser.value!!.id && !it.isRead })
            }

            repository.notifications.getOrElse { setError(it); return@launch }
                .onEach { (user, message) ->
                    _rooms.value[user]?.update { message }
                    _unreadAmount.value[user]?.update { amount -> amount + 1 }
                }
                .launchIn(viewModelScope)

            _uiState.value = Loaded
        }
    }
}