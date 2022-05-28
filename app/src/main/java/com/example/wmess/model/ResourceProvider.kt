package com.example.wmess.model

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(@ApplicationContext private val appContext: Context) {
    fun getString(@StringRes id: Int) =
        appContext.getString(id)
}