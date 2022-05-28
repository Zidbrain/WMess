package com.example.wmess.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmess.R
import com.example.wmess.model.WMessRepository
import com.example.wmess.model.modelclasses.LoginInfo
import com.example.wmess.model.modelclasses.LoginResult
import com.example.wmess.model.modelclasses.RegisterInfo
import com.example.wmess.model.modelclasses.RegisterResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterScreenUiState {
    object Unregistered : RegisterScreenUiState()
    data class Register(val accessToken: String) : RegisterScreenUiState()
    data class Error(@StringRes val errorMsg: Int) : RegisterScreenUiState()
    object InProgress : RegisterScreenUiState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: WMessRepository,
) : ViewModel() {
    private val _uiState = mutableStateOf<RegisterScreenUiState>(RegisterScreenUiState.Unregistered)
    val uiState: State<RegisterScreenUiState>
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
                is RegisterResult.Success -> when (val loginResult =
                    repository.login(LoginInfo(login, password))) {
                    is LoginResult.Success -> RegisterScreenUiState.Register(loginResult.accessToken)
                    else -> basicError
                }
                RegisterResult.UserAlreadyExists -> RegisterScreenUiState.Error(R.string.register_login_taken)
            }
        }
    }
}