package com.example.wmess.viewmodel

import androidx.annotation.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.R
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

sealed class RegisterScreenUiState {
    object Unregistered : RegisterScreenUiState()
    object Register : RegisterScreenUiState()
    data class Error(@StringRes val errorMsg: Int) : RegisterScreenUiState()
    object InProgress : RegisterScreenUiState()
}

class RegisterViewModel(
    private val repository: LoginRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<RegisterScreenUiState>(RegisterScreenUiState.Unregistered)
    val uiState: StateFlow<RegisterScreenUiState>
        get() = _uiState

    val login = mutableStateOf("")
    val password = mutableStateOf("")
    val username = mutableStateOf("")

    private val basicError =
        RegisterScreenUiState.Error(R.string.error_message)

    fun reset() {
        _uiState.value = RegisterScreenUiState.Unregistered
    }

    fun register() {
        _uiState.value = RegisterScreenUiState.InProgress
        viewModelScope.launch(Dispatchers.IO) {
            val login = login.value
            val password = password.value

            _uiState.value = when (
                repository.register(RegisterInfo(login, username.value, password))) {
                is RegisterResult.Error -> basicError
                is RegisterResult.Success -> when (repository.login(LoginInfo(login, password))) {
                    is LoginResult.Success -> RegisterScreenUiState.Register
                    else -> basicError
                }
                RegisterResult.UserAlreadyExists -> RegisterScreenUiState.Error(R.string.register_login_taken)
            }
        }
    }
}