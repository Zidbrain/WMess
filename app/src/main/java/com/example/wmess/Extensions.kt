package com.example.wmess

import com.google.gson.*
import com.google.gson.reflect.*
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

inline fun <reified T> Gson.fromJson(text: String): T =
    fromJson(text, object : TypeToken<T>() {}.type)

inline fun <K, V> MutableMap<K, V>.replace(key: K, transformation: (oldValue: V) -> V): Boolean {
    val value = this[key]
    if (value != null) {
        this[key] = transformation(value)
        return true
    }
    return false
}

inline fun <K, V> MutableMap<K, V>.replaceOrPut(
    key: K,
    newValue: V,
    transformation: (oldValue: V) -> V
) {
    val value = this[key]
    if (value != null)
        this[key] = transformation(value)
    else
        this[key] = newValue
}