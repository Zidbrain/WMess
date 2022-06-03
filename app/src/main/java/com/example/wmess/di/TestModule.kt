package com.example.wmess.di

import com.example.wmess.model.*
import dagger.*
import dagger.hilt.*
import dagger.hilt.android.components.*
import dagger.hilt.components.*

@Suppress("UNCHECKED_CAST")
@Module
@InstallIn(SingletonComponent::class, ActivityComponent::class, ActivityRetainedComponent::class)
object TestModule {

    @Provides
    fun provideLoginRepository(): LoginRepository =
        TestLoginRepository()

    @Provides
    fun provideMessengerRepositoryFactory(): MessengerRepositoryFactory<MessengerRepository> =
        TestMessengerRepositoryFactory as MessengerRepositoryFactory<MessengerRepository>
}