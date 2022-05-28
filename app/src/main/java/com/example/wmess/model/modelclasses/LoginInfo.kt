package com.example.wmess.model.modelclasses

data class LoginInfo(var login: String, var password: String)

sealed class LoginResult {
    data class Success(val accessToken: String) : LoginResult()
    object UserNotFound : LoginResult()
    data class Error(val error: String) : LoginResult()
}
