package com.example.wmess.viewmodel

import android.content.*
import android.net.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import coil.*
import coil.request.*
import coil.request.CachePolicy.*
import com.example.wmess.model.*
import com.example.wmess.model.modelclasses.*
import com.example.wmess.viewmodel.UiState.*
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
    private val repository: MessengerRepository,
    val imageLoader: ImageLoader
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(Initialized)
    val uiState: StateFlow<UiState> = _uiState

    lateinit var currentUser: User
        private set

    lateinit var fields: UserSettingsFields
        private set

    fun postFields() {
        viewModelScope.launch {
            repository.patchUser(fields.toUser())
        }
    }

    fun loadFields() {
        _uiState.value = Loading

        viewModelScope.launch {
            currentUser = repository.getCurrentUser().getOrElse {
                _uiState.value = Error(it)
                return@launch
            }
            fields = UserSettingsFields(currentUser)
            _uiState.value = Loaded
        }
    }

    fun changeAvatar(uri: Uri, context: Context, painterHandle: MutableState<ImageRequest>) {
        viewModelScope.launch(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri).use { stream ->
                repository.changeAvatar(stream!!).getOrThrow()

                painterHandle.value = ImageRequest.Builder(context)
                    .memoryCachePolicy(WRITE_ONLY)
                    .diskCachePolicy(WRITE_ONLY)
                    .data(currentUser.avatarURL)
                    .build()
            }
        }
    }
}