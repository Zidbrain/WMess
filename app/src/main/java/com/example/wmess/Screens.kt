package com.example.wmess

object Screens {
    const val Login = "login"
    const val CachedLogin = "cachedLogin"
    const val Register = "register"
    fun Messenger(accessToken: String) = "messenger/${accessToken}"
}