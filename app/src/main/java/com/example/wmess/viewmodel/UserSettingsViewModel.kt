package com.example.wmess.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UserSettingsViewModel.UiState.*
import dagger.assisted.*
import kotlinx.coroutines.*

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

class UserSettingsViewModel @AssistedInject constructor(
    repositoryFactory: MessengerRepositoryFactory<MessengerRepository>,
    @Assisted accessToken: String
) :
    ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(accessToken: String): UserSettingsViewModel
    }

    private val _uiState = mutableStateOf<UiState>(Loading)
    val uiState by _uiState

    open class UiState {
        object Loading : UiState()
        object Loaded : UiState()
    }

    private val repository = repositoryFactory.provide(accessToken)

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