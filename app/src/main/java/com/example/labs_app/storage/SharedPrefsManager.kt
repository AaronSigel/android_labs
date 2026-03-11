package com.example.labs_app.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

/** Имя файла резервной копии по умолчанию. */
const val DEFAULT_BACKUP_FILE_NAME = "characters_23.txt"

private const val PREFS_NAME = "labs_app_prefs"
private const val KEY_BACKUP_FILE_NAME = "backup_file_name"
private const val KEY_CURRENT_PAGE_GROUP = "current_character_page_group"
private const val DEFAULT_PAGE_GROUP = 23

/**
 * Менеджер имени файла резервной копии в SharedPreferences.
 */
class SharedPrefsManager(context: Context) {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBackupFileName(): String {
        val name = prefs.getString(KEY_BACKUP_FILE_NAME, DEFAULT_BACKUP_FILE_NAME) ?: DEFAULT_BACKUP_FILE_NAME
        Log.d(logTag, "getBackupFileName: $name")
        return name
    }

    fun setBackupFileName(fileName: String) {
        Log.d(logTag, "setBackupFileName: $fileName")
        prefs.edit { putString(KEY_BACKUP_FILE_NAME, fileName) }
    }

    fun getCurrentCharacterPageGroup(): Int =
        prefs.getInt(KEY_CURRENT_PAGE_GROUP, DEFAULT_PAGE_GROUP)

    fun setCurrentCharacterPageGroup(pageGroup: Int) {
        prefs.edit { putInt(KEY_CURRENT_PAGE_GROUP, pageGroup) }
    }
}
