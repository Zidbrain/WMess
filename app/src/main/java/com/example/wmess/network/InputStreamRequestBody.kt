package com.example.wmess.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.*
import java.io.*

class InputStreamRequestBody(
    private val stream: InputStream,
    private val listener: ProgressListener?
) : RequestBody() {

    override fun contentLength(): Long =
        stream.available().toLong()

    override fun contentType(): MediaType =
        "application/octet-stream".toMediaType()

    override fun writeTo(sink: BufferedSink) {
        var bytesWritten: Long = 0
        val length = stream.available()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        var read: Int
        while (stream.read(buffer).also { read = it } != -1) {
            bytesWritten += read
            listener?.onProgress(bytesWritten.toFloat() / length)
            sink.write(buffer, 0, read)
        }
    }

    companion object {
        fun InputStream.asRequestBody(listener: ProgressListener?) =
            InputStreamRequestBody(this, listener)
    }
}

fun interface ProgressListener {
    fun onProgress(progress: Float)
}