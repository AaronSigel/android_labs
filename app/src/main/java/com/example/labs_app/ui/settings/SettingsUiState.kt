package com.example.labs_app.ui.settings

import com.example.labs_app.storage.FileInfo

/**
 * Состояние экрана настроек: поля формы, статусы файлов, загрузки и ошибки.
 */
data class SettingsUiState(
    val userEmail: String = "",
    val notificationsEnabled: Boolean = true,
    val darkTheme: Boolean = false,
    val backupFileName: String = "characters_23.txt",
    val externalFileInfo: FileInfo = FileInfo(exists = false),
    val internalBackupInfo: FileInfo = FileInfo(exists = false),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isExporting: Boolean = false,
    val isDeleting: Boolean = false,
    val isRestoring: Boolean = false,
    val errorMessage: String? = null,
    /** Количество объектов в кэше для экспорта (из Home). */
    val exportDataCount: Int = 0,
    /** Текущий активный pageGroup списка персонажей (из SharedPrefs). */
    val currentPageGroup: Int = 23,
    /** Общее количество записей в локальной БД Room. */
    val totalRecordCount: Int = 0
) {
    val canCreateFile: Boolean get() = exportDataCount > 0 && !isExporting
    val canDeleteFile: Boolean get() = externalFileInfo.exists && !isDeleting
    val canRestore: Boolean get() = internalBackupInfo.exists && !isRestoring
}
