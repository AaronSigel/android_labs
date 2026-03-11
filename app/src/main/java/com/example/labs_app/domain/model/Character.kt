package com.example.labs_app.domain.model

/**
 * Доменная модель персонажа для отображения в UI.
 */
data class Character(
    val name: String,
    val culture: String,
    val born: String,
    val titles: List<String>,
    val aliases: List<String>,
    val playedBy: List<String>,
    val url: String? = null
)
