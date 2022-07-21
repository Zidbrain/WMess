package com.example.wmess

import java.net.*

inline fun <T> catchAll(onError: (error: String) -> T, block: () -> T): T {
    return try {
        block()
    } catch (ex: SocketTimeoutException) {
        onError("No connection")
    } catch (ex: Exception) {
        onError(ex.message ?: "An error occurred")
    }
}