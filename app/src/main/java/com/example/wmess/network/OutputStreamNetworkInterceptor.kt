package com.example.wmess.network

import com.example.wmess.network.ResponseProgressListener.Companion.asOutputStream
import okhttp3.*
import okhttp3.Interceptor.*
import java.io.*

class OutputStreamNetworkInterceptor: Interceptor {
    override fun intercept(chain: Chain): Response {
        val listener = chain.request().tag(ProgressListener::class.java)
        val stream = chain.request().tag(OutputStream::class.java)
        val response = chain.proceed(chain.request())

        if (listener != null && stream != null && response.body != null)
            return response.newBuilder()
                .body(response.body!!.asOutputStream(listener))
                .build()
        return response
    }
}