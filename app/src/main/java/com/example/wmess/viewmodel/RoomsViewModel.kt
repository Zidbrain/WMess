package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.RoomsViewModel.UiState.*
import dagger.assisted.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class RoomsViewModel @AssistedInject constructor(
    repositoryFactory: MessengerRepositoryFactory<MessengerRepository>,
    @Assisted accessToken: String
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(Loading)
    val uiState by _uiState

    private val repository = repositoryFactory.provide(accessToken)

    @AssistedFactory
    interface Factory {
        fun create(accessToken: String): RoomsViewModel
    }

    open class UiState {
        object Loading : UiState()
        object Loaded : UiState()
    }

    private val _currentUser = mutableStateOf(null as User?)
    val currentUser get() = _currentUser.value!!

    private val _rooms = mutableStateOf(emptyMap<User, MutableStateFlow<Message>>())
    val rooms: Map<User, StateFlow<Message>> by _rooms

    private val _unreadAmount = mutableStateOf(emptyMap<User, MutableStateFlow<Int>>())
    val unreadAmount: Map<User, StateFlow<Int>> by _unreadAmount

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