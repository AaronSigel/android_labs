package com.example.labs_app.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.labs_app.data.repository.CharacterRepository
import com.example.labs_app.storage.SharedPrefsManager

/**
 * Фабрика для HomeViewModel с внедрёнными CharacterRepository и SharedPrefsManager.
 */
class HomeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val repository by lazy { CharacterRepository(context) }
    private val sharedPrefs by lazy { SharedPrefsManager(context) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != HomeViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
        return HomeViewModel(repository, sharedPrefs) as T
    }
}
