package com.example.labs_app.storage

import android.util.Log
import com.example.labs_app.domain.model.Character

private const val LOG_TAG = "LabsApp/CharacterListCache"

/**
 * Кэш последнего загруженного списка персонажей для экспорта в Settings.
 * HomeViewModel записывает при успешной загрузке, SettingsViewModel читает при создании файла.
 */
object CharacterListCache {

    @Volatile
    private var list: List<Character> = emptyList()

    fun set(characters: List<Character>) {
        list = characters
        Log.d(LOG_TAG, "set: size=${characters.size}")
    }

    fun get(): List<Character> = list
}
