package com.example.wmess

import android.content.*
import android.net.*
import android.provider.*
import com.example.wmess.InputStreamRequestBody.Companion.asRequestBody
import com.google.gson.*
import com.google.gson.reflect.*
import okhttp3.*
import java.io.*
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

fun MultipartBody.Part.Companion.createFormData(
    name: String,
    filename: String?,
    stream: InputStream,
    progressListener: ProgressListener?
) =
    createFormData(name, filename, stream.asRequestBody(progressListener))

fun ContentResolver.getFileName(uri: Uri): String? {
    val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
    this.query(uri, projection, null, null, null)!!.use {
        if (it.moveToFirst())
            return it.getString(0)
    }
    return null
}

fun InputStream.transferTo(out: OutputStream) {
    val buffer = ByteArray(100 * 1024)
    var len: Int
    while (this.read(buffer).also { len = it } != -1){
        out.write(buffer, 0, len)
    }
}