package com.example.wmess.di

import com.example.wmess.model.TestRepository
import com.example.wmess.model.WMessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TestModule {

    @Provides
    fun provideWMessRepository(): WMessRepository =
        TestRepository()
}