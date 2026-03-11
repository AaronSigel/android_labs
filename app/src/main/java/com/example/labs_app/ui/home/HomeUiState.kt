package com.example.labs_app.ui.home

import com.example.labs_app.domain.model.Character

/**
 * Состояние экрана Home: список из Room, индикатор загрузки, ошибка.
 * Источник списка — Flow Room; сеть только наполняет БД.
 */
data class HomeUiState(
    val characters: List<Character> = emptyList(),
    val pageGroup: Int = 23,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
