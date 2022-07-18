package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UserSettingsViewModel.UiState.*
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
    private val repository: MessengerRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(Loading)
    val uiState: StateFlow<UiState> = _uiState

    open class UiState {
        object Loading : UiState()
        object Loaded : UiState()
    }

    lateinit var fields: UserSettingsFields
        private set

    fun postFields() {
        viewModelScope.launch {
            repository.patchUser(fields.toUser())
        }
    }

    fun loadFields() {
        viewModelScope.launch {
            fields = UserSettingsFields(repository.getCurrentUser())
            _uiState.value = Loaded
        }
    }
}