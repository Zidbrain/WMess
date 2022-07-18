package com.example.wmess.viewmodel

import androidx.lifecycle.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

sealed class CachedLoginScreenUiState {
    object Constructed : CachedLoginScreenUiState()
    object InProgress : CachedLoginScreenUiState()
    object Error : CachedLoginScreenUiState()
    object CacheMiss : CachedLoginScreenUiState()
    data class SignedIn(val accessToken: String) : CachedLoginScreenUiState()
}

class CachedLoginViewModel(
    private val repository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CachedLoginScreenUiState>(CachedLoginScreenUiState.Constructed)
    val uiState: StateFlow<CachedLoginScreenUiState> = _uiState

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = CachedLoginScreenUiState.InProgress
            val loginInfo = repository.getCachedLoginInfo()

            _uiState.value = if (loginInfo == null)
                CachedLoginScreenUiState.CacheMiss
            else {
                when (val result = repository.login(loginInfo)) {
                    is LoginResult.Success -> CachedLoginScreenUiState.SignedIn(result.accessToken)
                    else -> CachedLoginScreenUiState.Error
                }
            }
        }
    }
}