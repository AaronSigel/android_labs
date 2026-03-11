package com.example.labs_app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO персонажа из Game of Thrones API.
 * Поля могут приходить пустыми (пустая строка или пустой массив).
 */
@Serializable
data class CharacterDto(
    @SerialName("name") val name: String = "",
    @SerialName("culture") val culture: String = "",
    @SerialName("born") val born: String = "",
    @SerialName("titles") val titles: List<String> = emptyList(),
    @SerialName("aliases") val aliases: List<String> = emptyList(),
    @SerialName("playedBy") val playedBy: List<String> = emptyList(),
    @SerialName("url") val url: String? = null
)
