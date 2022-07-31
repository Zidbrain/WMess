package com.example.wmess.viewmodel

import androidx.annotation.*
import com.example.wmess.*

sealed interface UiState {
    object Initialized : UiState
    object Loading : UiState
    object Loaded : UiState
    data class Error(@StringRes val errorMsg: Int, val errorReason: String?) : UiState {
        constructor(errorReason: String) : this(R.string.error_message, errorReason)
        constructor(error: QueryResult.Error) : this(error.cause.message ?: "An error occurred")
    }
}