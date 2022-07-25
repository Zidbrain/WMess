package com.example.wmess.di

import coil.*
import com.example.wmess.*
import com.example.wmess.model.*
import com.example.wmess.model.api.*
import com.example.wmess.ui.formatters.*
import com.example.wmess.viewmodel.*
import com.google.gson.*
import com.google.gson.stream.*
import okhttp3.*
import org.koin.android.ext.koin.*
import org.koin.androidx.viewmodel.dsl.*
import org.koin.core.qualifier.*
import org.koin.dsl.*
import retrofit2.*
import retrofit2.converter.gson.*
import java.io.*
import java.time.*
import java.util.concurrent.TimeUnit.*

const val AUTH_CLIENT = "auth"
const val NO_AUTH_CLIENT = "noAuth"
const val BASE_URL = "https://zdmsg.duckdns.org/api/"

val testModule = module {
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    factory<MessengerRepository> {
        MessengerRepositoryImpl(
            get { it },
            get(named(AUTH_CLIENT)),
            get(),
            it.get()
        )
    }

    viewModel { LoginViewModel(get()) }
    viewModel { CachedLoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { RoomsViewModel(get { it }, get { it }) }
    viewModel { UserSettingsViewModel(get { it }, get { it }) }

    single {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .okHttpClient(get<OkHttpClient>(named(AUTH_CLIENT)) { it })
            .error(R.drawable.ic_baseline_error_outline_24)
            .build()
    }

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

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
    }

    single(named(NO_AUTH_CLIENT)) { OkHttpClient().newBuilder().build() }
    single(named(NO_AUTH_CLIENT)) {
        get<Retrofit.Builder>()
            .client(get(named(NO_AUTH_CLIENT)))
            .build()
    }

    single { get<Retrofit>(named(NO_AUTH_CLIENT)).create(AuthApi::class.java) }

    single(named(AUTH_CLIENT)) { params ->
        OkHttpClient().newBuilder().addInterceptor {
            it.proceed(
                it.request().newBuilder()
                    .cacheControl(CacheControl.Builder().maxAge(3, SECONDS).build())
                    .addHeader("Authorization", "Bearer ${params.get<String>()}")
                    .build()
            )
        }
            .cache(Cache(File("/cache/http_cache"), 2 * 1024L * 1024L))
            .build()
    }
    single(named(AUTH_CLIENT)) {
        get<Retrofit.Builder>()
            .client(get(named(AUTH_CLIENT)) { it })
            .build()
    }

    single { get<Retrofit>(named(AUTH_CLIENT)) { it }.create(MessengerApi::class.java) }

    single { MessengerWebSocketListener(get()) }
}