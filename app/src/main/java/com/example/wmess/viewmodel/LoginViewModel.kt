package com.example.wmess.viewmodel

import androidx.annotation.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.wmess.R
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

sealed class LoginScreenUiState {
    object SignedOut : LoginScreenUiState()
    data class Error(@StringRes val errorMsg: Int) : LoginScreenUiState()
    data class SignedIn(val accessToken: String) : LoginScreenUiState()
    object InProgress : LoginScreenUiState()
}

class LoginViewModel(
    private val repository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginScreenUiState>(LoginScreenUiState.SignedOut)
    val uiState: StateFlow<LoginScreenUiState>
        get() = _uiState

    val login = mutableStateOf("")
    val password = mutableStateOf("")

    fun reset() {
        _uiState.value = LoginScreenUiState.SignedOut
    }

    fun doLogin() {
        _uiState.value = LoginScreenUiState.InProgress
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                when (val result = repository.login(LoginInfo(login.value, password.value))) {
                    is LoginResult.Success -> LoginScreenUiState.SignedIn(result.accessToken)
                    is LoginResult.Error -> LoginScreenUiState.Error(R.string.error_message)
                    LoginResult.UserNotFound -> LoginScreenUiState.Error(R.string.wrong_login_error)
                }
        }
    }
}