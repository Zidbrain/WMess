package com.example.wmess.model.modelclasses

import com.google.gson.annotations.*

data class LoginInfo(
    @SerializedName("username") var login: String,
    var password: String
)

data class AuthApiLoginResponse(val accessToken: String)

sealed class LoginResult {
    object Success : LoginResult()
    object UserNotFound : LoginResult()
    data class Error(val error: String) : LoginResult()
}
