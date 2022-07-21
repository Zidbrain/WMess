package com.example.wmess.model.modelclasses

import com.google.gson.annotations.*

data class RegisterInfo(
    @SerializedName("username") var login: String,
    @SerializedName("nickname") var username: String,
    var password: String
)

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    object UserAlreadyExists : RegisterResult()
    data class Error(val error: String) : RegisterResult()
}
