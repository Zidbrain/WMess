package com.example.wmess.di

import com.example.wmess.model.*
import com.example.wmess.viewmodel.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.dsl.*

val testModule = module {
    single<LoginRepository> { TestLoginRepository() }
    single<MessengerRepository> { TestMessengerRepository(it.get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { CachedLoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { RoomsViewModel(get() { it }) }
    viewModel { UserSettingsViewModel(get { it }) }
}