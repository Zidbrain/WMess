package com.example.wmess.di

import coil.*
import com.example.wmess.*
import com.example.wmess.model.*
import com.example.wmess.model.api.*
import com.example.wmess.network.*
import com.example.wmess.ui.formatters.*
import com.example.wmess.viewmodel.*
import com.google.gson.*
import com.google.gson.stream.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.Response
import org.koin.android.ext.koin.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.core.module.*
import org.koin.core.qualifier.*
import org.koin.dsl.*
import retrofit2.*
import retrofit2.converter.gson.*
import java.time.*

const val AUTH_CLIENT = "auth"
const val NO_AUTH_CLIENT = "noAuth"
const val BASE_URL = "https://zdmsg.duckdns.org/api/"

val testModule = module {
    provideRepositories()
    provideViewModels()

    provideImageLoader()
    provideGson()

    provideRetrofitBuilder()
    provideOkHttpClient()
    provideRetrofit()
    provideApi()

    provideWebSocket()
}

fun Module.provideWebSocket() {
    single { MessengerWebSocketListener(get()) }
}

fun Module.provideOkHttpClient() {
    single(named(NO_AUTH_CLIENT)) { OkHttpClient().newBuilder().build() }

    single(named(AUTH_CLIENT)) {
        val accessTokenHolder: AccessTokenHolder = get()

        OkHttpClient().newBuilder().addInterceptor {
            fun proceedWithAuth(): Response =
                it.proceed(
                    it.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${accessTokenHolder.accessToken}")
                        .build()
                )

            val response = proceedWithAuth()

            if (response.code != 401)
                response
            else {
                response.close()
                runBlocking {
                    accessTokenHolder.retrieve()
                        .onFailure { exception ->
                            throw Exception("Error retrieving access token", exception.cause)
                        }
                }
                proceedWithAuth()
            }
        }
            .addNetworkInterceptor(OutputStreamNetworkInterceptor())
            .build()
    }
}

fun Module.provideApi() {
    single { get<Retrofit>(named(NO_AUTH_CLIENT)).create(AuthApi::class.java) }
    single { get<Retrofit>(named(AUTH_CLIENT)).create(MessengerApi::class.java) }
}

fun Module.provideRetrofit() {
    single(named(NO_AUTH_CLIENT)) {
        get<Retrofit.Builder>()
            .client(get(named(NO_AUTH_CLIENT)))
            .build()
    }

    single(named(AUTH_CLIENT)) {
        get<Retrofit.Builder>()
            .client(get(named(AUTH_CLIENT)))
            .build()
    }
}

fun Module.provideRetrofitBuilder() {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
    }
}

fun Module.provideGson() {
    single {
        Gson().newBuilder()
            .registerTypeAdapter(Instant::class.java, object : TypeAdapter<Instant>() {
                override fun write(out: JsonWriter?, value: Instant?) {
                    out!!.value(value!!.toFullString())
                }

                override fun read(`in`: JsonReader?): Instant {
                    return `in`!!.nextString().toInstant()
                }

            }).setLenient().create()
    }
}

fun Module.provideImageLoader() {
    single {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .okHttpClient(get<OkHttpClient>(named(AUTH_CLIENT)))
            .error(R.drawable.ic_baseline_error_outline_24)
            .build()
    }
}

fun Module.provideRepositories() {
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    single<MessengerRepository> {
        MessengerRepositoryImpl(
            get(),
            get(named(AUTH_CLIENT)),
            get(),
            get(),
            get()
        )
    }
}

fun Module.provideViewModels() {
    viewModel { LoginViewModel(get()) }
    viewModel { CachedLoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { RoomsViewModel(get(), get()) }
    viewModel { UserSettingsViewModel(get(), get()) }
    viewModel { MessageBoardViewModel(get(), it[0], it[1]) }
    viewModel { CreateRoomViewModel(get(), it.get()) }
}