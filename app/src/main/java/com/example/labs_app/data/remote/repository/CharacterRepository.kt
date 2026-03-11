package com.example.labs_app.data.remote.repository

import android.util.Log
import com.example.labs_app.data.remote.api.CharacterApi
import com.example.labs_app.data.remote.dto.CharacterDto
import com.example.labs_app.domain.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Диапазон ID для варианта 23: 1101..1150. */
private const val VARIANT_23_ID_START = 1101
private const val VARIANT_23_ID_END = 1150

/**
 * Репозиторий персонажей Game of Thrones API.
 * Загружает персонажей по диапазону ID 1101..1150 (50 запросов).
 */
class CharacterRepository {

    private val logTag: String get() = "LabsApp/CharacterRepository"

    private val api: CharacterApi by lazy {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        val okHttp = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CharacterApi::class.java)
    }

    /**
     * Загружает персонажей для варианта 23 (ID 1101..1150).
     * Выполняет до 50 запросов, собирает успешные результаты.
     * При частичных сбоях возвращает только успешно загруженные объекты.
     */
    suspend fun getCharactersForVariant23(): List<Character> = withContext(Dispatchers.IO) {
        (VARIANT_23_ID_START..VARIANT_23_ID_END).mapNotNull { id ->
            runCatching {
                val dto = api.getCharacter(id)
                Log.d(logTag, "API character id=$id: name=${dto.name}, culture=${dto.culture}, born=${dto.born}, titles=${dto.titles}, aliases=${dto.aliases}, playedBy=${dto.playedBy}, url=${dto.url}")
                dto.toDomain()
            }.onFailure { e ->
                Log.w(logTag, "API failed id=$id: ${e.message}", e)
            }.getOrNull()
        }.also { list ->
            Log.d(logTag, "getCharactersForVariant23: loaded ${list.size} characters")
        }
    }

    /**
     * Потоковая загрузка персонажей 1101..1150. Эмитит каждого персонажа по мере
     * получения ответа от API (запросы выполняются параллельно).
     */
    fun getCharactersForVariant23Flow(): Flow<Character> = channelFlow {
        coroutineScope {
            (VARIANT_23_ID_START..VARIANT_23_ID_END).forEach { id ->
                launch {
                    val result = withContext(Dispatchers.IO) {
                        runCatching {
                            val dto = api.getCharacter(id)
                            Log.d(logTag, "API character id=$id: name=${dto.name}, culture=${dto.culture}, born=${dto.born}, titles=${dto.titles}, aliases=${dto.aliases}, playedBy=${dto.playedBy}, url=${dto.url}")
                            dto.toDomain()
                        }.onFailure { e ->
                            Log.w(logTag, "API failed id=$id: ${e.message}", e)
                        }.getOrNull()
                    }
                    result?.let { send(it) }
                }
            }
        }
    }

    private fun CharacterDto.toDomain(): Character = Character(
        name = name,
        culture = culture,
        born = born,
        titles = titles,
        aliases = aliases,
        playedBy = playedBy,
        url = url
    )
}

private const val BASE_URL = "https://anapioficeandfire.com/"
