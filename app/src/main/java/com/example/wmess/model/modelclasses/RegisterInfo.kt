package com.example.wmess.model.modelclasses

data class RegisterInfo(var login: String, var username: String, var password: String)

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    object UserAlreadyExists : RegisterResult()
    data class Error(val error: String) : RegisterResult()
}
