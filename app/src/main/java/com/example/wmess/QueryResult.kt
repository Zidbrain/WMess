package com.example.wmess

import com.example.wmess.QueryResult.*
import retrofit2.*
import java.net.*

sealed class QueryResult<out T> {

    @Suppress("UNCHECKED_CAST")
    inline fun <V> map(transformation: (T) -> V): QueryResult<V> =
        if (this is Success<T>) Success(transformation(data))
        else this as QueryResult<V>


    inline fun onSuccess(block: (data: T) -> Unit): QueryResult<T> {
        if (this is Success<T>) block(data)
        return this
    }

    inline fun onFailure(block: (Error) -> Unit): QueryResult<T> {
        if (this !is Success<T>) block(this as Error)
        return this
    }

    inline fun getOrElse(block: (Error) -> Nothing): T {
        if (this is Success<T>) return data
        else block(this as Error)
    }

    inline fun switch(onSuccess: (T) -> Unit, onFailure: (Error) -> Unit) {
        if (this is Success<T>) onSuccess(data)
        else onFailure(this as Error)
    }

    data class Success<out T>(val data: T) : QueryResult<T>()
    class Unauthorized : ErrorCode(401, "Unauthorized")
    open class ErrorCode(val errorCode: Int, error: String) : Error(error)
    open class Error(val error: String) : QueryResult<Nothing>()
}

fun <T> resultOf(value: T): Success<T> =
    Success(value)

inline fun <T> safeCall(
    block: () -> Response<T>
): QueryResult<T> =
    try {
        block().toQueryResult()
    } catch (ex: SocketTimeoutException) {
        Error("Error establishing connection")
    } catch (ex: Exception) {
        Error(ex.message ?: "An error occurred")
    }

fun <T> Response<T>.toQueryResult(): QueryResult<T> {
    val body = body()
    return if (body == null)
        when (val code = code()) {
            401 -> Unauthorized()
            else -> ErrorCode(code, "Server responded with the code $code")
        }
    else
        Success(body)
}