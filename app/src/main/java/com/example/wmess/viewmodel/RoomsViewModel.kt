package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.RoomsViewModel.UiState.*
import dagger.assisted.*
import kotlinx.coroutines.*

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

    private val _rooms = mutableStateOf(emptyMap<User, Message>())
    val rooms by _rooms

    fun loadRooms() {
        viewModelScope.launch {
            _currentUser.value = repository.getCurrentUser()

            _rooms.value = repository.getHistoryByUsers().asIterable().associate {
                it.key to it.value.last()
            }

            _uiState.value = Loaded
        }
    }
}