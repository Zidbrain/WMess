package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class CreateRoomViewModel(
    private val repository: MessengerRepository,
    private val currentUserId: UUID
) : ViewModel() {

    var searchText by mutableStateOf("")

    private var allUsers: List<User> = emptyList()
    private val _users = mutableStateListOf<User>()
    val users: List<User> by lazy {
        viewModelScope.launch {
            allUsers = repository.getUsers().getOrElse { uiState = Error(it.error); return@launch }
                .filter { user -> user.id != currentUserId }
            _users.addAll(allUsers)
        }
        _users
    }

    var uiState: UiState by mutableStateOf(Initialized)
        private set

    init {
        snapshotFlow { searchText }.onEach {
            _users.clear()
            if (it == "")
                _users.addAll(allUsers)
            else
                _users.addAll(allUsers.filter { user -> user.nickname.contains(it) })
        }.launchIn(viewModelScope)
    }
}