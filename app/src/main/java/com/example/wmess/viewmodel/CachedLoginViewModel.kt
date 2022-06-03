package com.example.wmess.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmess.model.LoginRepository
import com.example.wmess.model.modelclasses.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CachedLoginScreenUiState {
    object Constructed : CachedLoginScreenUiState()
    object InProgress : CachedLoginScreenUiState()
    object Error : CachedLoginScreenUiState()
    object CacheMiss : CachedLoginScreenUiState()
    data class SignedIn(val accessToken: String) : CachedLoginScreenUiState()
}

@HiltViewModel
class CachedLoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _uiState =
        mutableStateOf<CachedLoginScreenUiState>(CachedLoginScreenUiState.Constructed)
    val uiState: State<CachedLoginScreenUiState>
        get() = _uiState

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = CachedLoginScreenUiState.InProgress
            val loginInfo = repository.getCachedLoginInfo()

            if (loginInfo == null)
                _uiState.value = CachedLoginScreenUiState.CacheMiss
            else {
                _uiState.value = when (val result = repository.login(loginInfo)) {
                    is LoginResult.Success -> CachedLoginScreenUiState.SignedIn(result.accessToken)
                    else -> CachedLoginScreenUiState.Error
                }
            }
        }
    }
}