package com.example.labs_app.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labs_app.storage.CharacterListCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel экрана настроек. Загружает состояние при открытии, сохраняет настройки,
 * выполняет экспорт/удаление/восстановление файла в IO.
 */
class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    /**
     * Загрузить настройки и информацию о файлах. Вызывать при открытии экрана.
     */
    fun load() {
        Log.d(logTag, "load: начало загрузки настроек")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.loadInitialState()
            }.fold(
                onSuccess = { initial ->
                    val exportCount = CharacterListCache.get().size
                    Log.d(logTag, "load: успех, exportDataCount=$exportCount, backupFileName=${initial.backupFileName}")
                    _state.value = initial.copy(
                        isLoading = false,
                        exportDataCount = exportCount
                    )
                },
                onFailure = { e ->
                    Log.e(logTag, "load: ошибка", e)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Ошибка загрузки"
                        )
                    }
                }
            )
        }
    }

    fun saveSettings() {
        val s = _state.value
        Log.d(logTag, "saveSettings: userEmail=${s.userEmail}, darkTheme=${s.darkTheme}, backupFileName=${s.backupFileName}")
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.saveSettings(
                    userEmail = s.userEmail,
                    notificationsEnabled = s.notificationsEnabled,
                    darkTheme = s.darkTheme,
                    backupFileName = s.backupFileName
                )
                refreshFileInfos(s.backupFileName)
            }.fold(
                onSuccess = {
                    Log.d(logTag, "saveSettings: успех")
                    _state.update {
                        it.copy(isSaving = false, exportDataCount = CharacterListCache.get().size)
                    }
                },
                onFailure = { e ->
                    Log.e(logTag, "saveSettings: ошибка", e)
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = e.message ?: "Ошибка сохранения"
                        )
                    }
                }
            )
        }
    }

    fun updateUserEmail(value: String) { _state.update { it.copy(userEmail = value) } }
    fun updateNotificationsEnabled(value: Boolean) { _state.update { it.copy(notificationsEnabled = value) } }
    fun updateDarkTheme(value: Boolean) { _state.update { it.copy(darkTheme = value) } }
    fun updateBackupFileName(value: String) { _state.update { it.copy(backupFileName = value) } }
    fun clearError() { _state.update { it.copy(errorMessage = null) } }
    fun setError(message: String?) { _state.update { it.copy(errorMessage = message) } }

    fun createOrOverwriteFile() {
        val fileName = _state.value.backupFileName.ifBlank { "characters_23.txt" }
        val characters = CharacterListCache.get()
        Log.d(logTag, "createOrOverwriteFile: fileName=$fileName, charactersCount=${characters.size}")
        if (characters.isEmpty()) {
            Log.w(logTag, "createOrOverwriteFile: нет данных для экспорта")
            _state.update { it.copy(errorMessage = "Нет данных для экспорта. Загрузите список на главном экране.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isExporting = true, errorMessage = null) }
            repository.exportToExternal(fileName, characters)
                .fold(
                    onSuccess = {
                        Log.d(logTag, "createOrOverwriteFile: успех")
                        refreshFileInfos(fileName)
                        _state.update { it.copy(isExporting = false) }
                    },
                    onFailure = { e ->
                        Log.e(logTag, "createOrOverwriteFile: ошибка", e)
                        _state.update {
                            it.copy(
                                isExporting = false,
                                errorMessage = e.message ?: "Ошибка экспорта"
                            )
                        }
                    }
                )
        }
    }

    fun deleteExternalFile() {
        val fileName = _state.value.backupFileName.ifBlank { "characters_23.txt" }
        Log.d(logTag, "deleteExternalFile: fileName=$fileName")
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, errorMessage = null) }
            repository.deleteExternalWithBackup(fileName)
                .fold(
                    onSuccess = {
                        Log.d(logTag, "deleteExternalFile: успех")
                        refreshFileInfos(fileName)
                        _state.update { it.copy(isDeleting = false) }
                    },
                    onFailure = { e ->
                        Log.e(logTag, "deleteExternalFile: ошибка", e)
                        _state.update {
                            it.copy(
                                isDeleting = false,
                                errorMessage = e.message ?: "Ошибка удаления"
                            )
                        }
                    }
                )
        }
    }

    fun restoreFromBackup() {
        val fileName = _state.value.backupFileName.ifBlank { "characters_23.txt" }
        Log.d(logTag, "restoreFromBackup: fileName=$fileName")
        viewModelScope.launch {
            _state.update { it.copy(isRestoring = true, errorMessage = null) }
            repository.restoreFromBackup(fileName)
                .fold(
                    onSuccess = {
                        Log.d(logTag, "restoreFromBackup: успех")
                        refreshFileInfos(fileName)
                        _state.update { it.copy(isRestoring = false) }
                    },
                    onFailure = { e ->
                        Log.e(logTag, "restoreFromBackup: ошибка", e)
                        _state.update {
                            it.copy(
                                isRestoring = false,
                                errorMessage = e.message ?: "Ошибка восстановления"
                            )
                        }
                    }
                )
        }
    }

    private suspend fun refreshFileInfos(fileName: String) {
        val external = repository.getExternalFileInfo(fileName)
        val internal = repository.getInternalBackupInfo(fileName)
        _state.update {
            it.copy(externalFileInfo = external, internalBackupInfo = internal)
        }
    }
}
