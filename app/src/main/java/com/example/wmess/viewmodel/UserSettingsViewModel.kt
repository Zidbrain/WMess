package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import coil.*
import com.example.wmess.R
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UiState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class UserSettingsFields(private val user: User) {

    val nickname = mutableStateOf(user.nickname)
    val status = mutableStateOf(user.status ?: "")
    val phoneNumber = mutableStateOf(user.phoneNumber ?: "")

    fun toUser(): User {
        user.nickname = nickname.value
        user.status = status.value
        user.phoneNumber = phoneNumber.value

        return user
    }
}

class UserSettingsViewModel(
    private val repository: MessengerRepository,
    val imageLoader: ImageLoader
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(Initialized)
    val uiState: StateFlow<UiState> = _uiState

    lateinit var currentUser: User
        private set

    lateinit var fields: UserSettingsFields
        private set

    fun postFields() {
        viewModelScope.launch {
            repository.patchUser(fields.toUser())
        }
    }

    fun loadFields() {
        _uiState.value = Loading

        viewModelScope.launch {
            currentUser = repository.getCurrentUser().getOrElse {
                _uiState.value = Error(R.string.error_message, it.error)
                return@launch
            }
            fields = UserSettingsFields(currentUser)
            _uiState.value = Loaded
        }
    }
}