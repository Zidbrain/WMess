package com.example.wmess.viewmodel

import androidx.annotation.*

sealed interface UiState {
    object Initialized : UiState
    object Loading : UiState
    object Loaded : UiState
    data class Error(@StringRes val errorMsg: Int, val errorReason: String?) : UiState
}