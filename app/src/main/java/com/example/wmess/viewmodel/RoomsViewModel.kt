package com.example.wmess.viewmodel

import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.RoomsViewModel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class RoomsViewModel(
    private val repository: MessengerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState

    open class UiState {
        object Loading : UiState()
        object Loaded : UiState()
    }

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

    fun loadRooms() {
        viewModelScope.launch {
            _currentUser.value = repository.getCurrentUser()

            val history = repository.getHistoryByUsers().asIterable()
            _rooms.value = history.associate { (user, messages) ->
                user to MutableStateFlow(messages.last())
            }
            _unreadAmount.value = history.associate { (user, messages) ->
                user to MutableStateFlow(messages.count { !it.isRead })
            }

            repository.notifications
                .onEach { (user, message) ->
                    _rooms.value[user]?.update { message }
                    _unreadAmount.value[user]?.update { amount -> amount + 1 }
                }
                .launchIn(viewModelScope)

            _uiState.value = Loaded
        }
    }
}