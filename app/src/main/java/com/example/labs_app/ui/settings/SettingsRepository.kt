package com.example.labs_app.ui.settings

import android.util.Log
import com.example.labs_app.domain.model.Character
import com.example.labs_app.storage.BackupFileManager
import com.example.labs_app.storage.FileInfo
import com.example.labs_app.storage.PreferencesDataStoreManager
import com.example.labs_app.storage.SharedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Репозиторий настроек: объединяет DataStore, SharedPreferences и операции с файлом резервной копии.
 */
class SettingsRepository(
    private val dataStoreManager: PreferencesDataStoreManager,
    private val sharedPrefsManager: SharedPrefsManager,
    private val backupFileManager: BackupFileManager
) {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    /**
     * Загружает все настройки и актуальные данные о файлах для начального отображения.
     */
    suspend fun loadInitialState(): SettingsUiState = withContext(Dispatchers.IO) {
        Log.d(logTag, "loadInitialState")
        val userEmail = dataStoreManager.getUserEmail()
        val notificationsEnabled = dataStoreManager.getNotificationsEnabled()
        val darkTheme = dataStoreManager.getDarkTheme()
        val backupFileName = sharedPrefsManager.getBackupFileName()
        val externalFileInfo = backupFileManager.getExternalFileInfo(backupFileName)
        val internalBackupInfo = backupFileManager.getInternalBackupInfo(backupFileName)
        Log.d(logTag, "loadInitialState: backupFileName=$backupFileName, externalExists=${externalFileInfo.exists}, internalExists=${internalBackupInfo.exists}")
        SettingsUiState(
            userEmail = userEmail,
            notificationsEnabled = notificationsEnabled,
            darkTheme = darkTheme,
            backupFileName = backupFileName,
            externalFileInfo = externalFileInfo,
            internalBackupInfo = internalBackupInfo,
            isLoading = false
        )
    }

    suspend fun saveSettings(
        userEmail: String,
        notificationsEnabled: Boolean,
        darkTheme: Boolean,
        backupFileName: String
    ) = withContext(Dispatchers.IO) {
        Log.d(logTag, "saveSettings: backupFileName=$backupFileName")
        dataStoreManager.setAll(userEmail, notificationsEnabled, darkTheme)
        sharedPrefsManager.setBackupFileName(backupFileName)
    }

    suspend fun exportToExternal(fileName: String, characters: List<Character>): Result<Unit> {
        Log.d(logTag, "exportToExternal: fileName=$fileName, count=${characters.size}")
        return backupFileManager.exportToExternal(fileName, characters)
    }

    suspend fun deleteExternalWithBackup(fileName: String): Result<Unit> {
        Log.d(logTag, "deleteExternalWithBackup: fileName=$fileName")
        return backupFileManager.deleteExternalWithInternalBackup(fileName)
    }

    suspend fun restoreFromBackup(fileName: String): Result<Unit> {
        Log.d(logTag, "restoreFromBackup: fileName=$fileName")
        return backupFileManager.restoreExternalFromInternalBackup(fileName)
    }

    suspend fun getExternalFileInfo(fileName: String): FileInfo =
        backupFileManager.getExternalFileInfo(fileName)

    suspend fun getInternalBackupInfo(fileName: String): FileInfo =
        backupFileManager.getInternalBackupInfo(fileName)
}
