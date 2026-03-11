package com.example.labs_app.ui.home

import com.example.labs_app.domain.model.Character

/**
 * Состояние экрана Home: загрузка, успех с данными или ошибка.
 */
sealed class HomeUiState {
    data object Loading : HomeUiState()
    /** @param isComplete false — загрузка ещё идёт, элементы приходят в реальном времени */
    data class Success(val characters: List<Character>, val isComplete: Boolean = true) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
