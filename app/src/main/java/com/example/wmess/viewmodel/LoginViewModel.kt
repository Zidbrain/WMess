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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginScreenUiState {
    object SignedOut : LoginScreenUiState()
    data class Error(@StringRes val errorMsg: Int) : LoginScreenUiState()
    data class SignedIn(val accessToken: String) : LoginScreenUiState()
    object InProgress : LoginScreenUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: WMessRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<LoginScreenUiState>(LoginScreenUiState.SignedOut)
    val uiState: State<LoginScreenUiState>
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