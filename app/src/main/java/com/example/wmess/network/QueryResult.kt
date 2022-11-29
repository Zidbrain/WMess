package com.example.wmess.network

import com.example.wmess.network.QueryResult.*
import retrofit2.*

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

    fun getOrThrow(): T =
        if (this is Success<T>) data
        else throw (this as Error).cause

    inline fun getOrErrorCode(block: (ErrorCode) -> Nothing): T =
        when (this) {
            is Success<T> -> data
            is ErrorCode -> block(this)
            else -> throw ((this as Error).cause)
        }

    inline fun switch(onSuccess: (T) -> Unit, onFailure: (Error) -> Unit) {
        if (this is Success<T>) onSuccess(data)
        else onFailure(this as Error)
    }

    data class Success<out T>(val data: T) : QueryResult<T>()
    class Unauthorized(response: Response<*>) : ErrorCode(response)
    open class ErrorCode(val response: Response<*>) : Error(ErrorCodeResponseException(response)) {
        val errorCode = response.code()
    }

    open class Error(val cause: Throwable) : QueryResult<Nothing>()
}

class ErrorCodeResponseException(val response: Response<*>) : Exception(
    """Server responded with the code ${response.code()}. With message:
        ${response.errorBody()?.string() ?: response.message()}"""
)

fun <T> resultOf(value: T): Success<T> =
    Success(value)

inline fun <T> safeCall(
    block: () -> Response<T>
): QueryResult<T> =
    try {
        block().toQueryResult()
    } catch (ex: Exception) {
        Error(ex)
    }

@Suppress("UNCHECKED_CAST")
fun <T> Response<T>.toQueryResult(): QueryResult<T> {
    val body = body()
    return if (!isSuccessful)
        when (code()) {
            401 -> Unauthorized(this)
            else -> ErrorCode(this)
        }
    else if (body == null)
        Success<Void?>(null) as QueryResult<T>
    else
        Success(body)
}

inline fun <T> query(block: () -> T): QueryResult<T> =
    try {
        Success(block())
    } catch (ex: Throwable) {
        Error(ex)
    }