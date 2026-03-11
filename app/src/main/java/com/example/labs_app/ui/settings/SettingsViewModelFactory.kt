package com.example.labs_app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Фабрика для создания SettingsViewModel с внедрённым репозиторием.
 */
class SettingsViewModelFactory(
    private val repository: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != SettingsViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return SettingsViewModel(repository) as T
    }
}
