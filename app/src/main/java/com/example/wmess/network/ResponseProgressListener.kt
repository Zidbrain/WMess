package com.example.wmess.network

import com.example.wmess.*
import okhttp3.*
import okio.*

class ResponseProgressListener(
    private val delegate: ResponseBody,
    private val listener: ProgressListener?
) : ResponseBody() {
    override fun contentLength(): Long =
        delegate.contentLength()

    override fun contentType(): MediaType? =
        delegate.contentType()

    override fun source(): BufferedSource =
        ListenBufferedSource(delegate.source()).buffer()

    private inner class ListenBufferedSource(private val bufferedSource: BufferedSource) :
        ForwardingSource(bufferedSource) {

        private var read = 0
        private val length = contentLength()
        private var bytesRead = 0L
        private val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        override fun read(sink: Buffer, byteCount: Long): Long {
            bufferedSource.inputStream().let { stream ->
                while (stream.read(buffer).also { read = it } != -1) {
                    bytesRead += read
                    listener?.onProgress(bytesRead.toFloat() / length)
                    sink.write(buffer, 0, read)
                }
            }

            return bytesRead
        }
    }

    override fun close() {
        super.close()
        delegate.close()
    }

    companion object {
        fun ResponseBody.asOutputStream(listener: ProgressListener?) =
            ResponseProgressListener(this, listener)
    }
}