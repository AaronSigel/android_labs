package com.example.labs_app.data.remote

import android.util.Log
import com.example.labs_app.data.remote.api.CharacterApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://anapioficeandfire.com/"
private const val CONNECT_TIMEOUT_SEC = 15L
private const val READ_TIMEOUT_SEC = 30L
private const val LOG_TAG = "LabsApp/API"

/**
 * Создаёт экземпляр CharacterApi для запросов к An API of Ice and Fire.
 */
object RetrofitProvider {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val loggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        Log.d(LOG_TAG, "→ ${request.method} ${request.url}")
        val response = chain.proceed(request)
        Log.d(LOG_TAG, "← ${response.code} ${request.url}")
        response
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val characterApi: CharacterApi by lazy {
        Log.d(LOG_TAG, "инициализация CharacterApi, baseUrl=$BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory(requireNotNull("application/json".toMediaTypeOrNull()))
            )
            .build()
            .create(CharacterApi::class.java)
            .also { Log.d(LOG_TAG, "CharacterApi создан") }
    }
}
