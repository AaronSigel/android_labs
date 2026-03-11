package com.example.labs_app.data.remote.api

import com.example.labs_app.data.remote.dto.CharacterDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API персонажей Game of Thrones (An API of Ice and Fire).
 * Базовый URL: https://anapioficeandfire.com
 */
interface CharacterApi {

    @GET("api/characters/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto
}
