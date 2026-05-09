package com.turkcell.data.di

import com.turkcell.data.remote.AuthApi
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.core.domain.AuthRepository
import org.koin.dsl.module
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val dataModule = module {

    // Retrofit instance
    single<Retrofit> {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        Retrofit.Builder()
            .baseUrl("https://10.0.2.2:8080/") //TODO baseUrl eklenecek. şimdilik ".baseUrl("https://10.0.2.2:8080/")" kullanarak hata alma.
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // AuthApi
    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    // AuthRepository implementasyonu
    single<AuthRepository> {
        AuthRepositoryImpl(authApi = get())
    }
}
