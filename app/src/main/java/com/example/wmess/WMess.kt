package com.example.wmess

import android.app.*
import com.example.wmess.di.*
import org.koin.android.ext.koin.*
import org.koin.core.context.*

class WMess : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WMess)
            modules(testModule)
        }
    }
}