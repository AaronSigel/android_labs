package com.example.labs_app.storage

import android.content.Context
import android.os.Build
import android.util.Log
import android.os.Environment
import com.example.labs_app.domain.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val INTERNAL_BACKUP_DIR = "backup"
private const val DATE_FORMAT = "dd.MM.yyyy HH:mm:ss"

/**
 * Управление экспортом списка в .txt во внешнее хранилище и резервной копией во внутреннем.
 * Все операции выполняются на Dispatchers.IO.
 */
class BackupFileManager(private val context: Context) {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    /**
     * Формирует человекочитаемый текст из списка персонажей и сохраняет во внешнюю директорию
     * (приоритет Documents, иначе Downloads). Имя файла — из параметра.
     */
    suspend fun exportToExternal(
        fileName: String,
        characters: List<Character>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val content = formatCharactersAsTxt(characters)
            val dir = getExternalDir()
                ?: throw IllegalStateException("Внешнее хранилище недоступно")
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(content.toByteArray(Charsets.UTF_8)) }
            Log.d(logTag, "exportToExternal: успех path=${file.absolutePath}, size=${content.length}")
            Unit
        }.onFailure { e ->
            Log.e(logTag, "exportToExternal: ошибка fileName=$fileName", e)
        }
    }

    /**
     * Информация о внешнем файле: существует ли, имя, путь, размер, дата изменения.
     */
    suspend fun getExternalFileInfo(fileName: String): FileInfo = withContext(Dispatchers.IO) {
        val dir = getExternalDir() ?: return@withContext FileInfo(exists = false, fileName = fileName)
        val file = File(dir, fileName)
        if (!file.exists() || !file.isFile) {
            return@withContext FileInfo(exists = false, fileName = fileName, fullPath = file.absolutePath)
        }
        FileInfo(
            exists = true,
            fileName = file.name,
            fullPath = file.absolutePath,
            sizeBytes = file.length(),
            lastModifiedTime = file.lastModified()
        )
    }

    /**
     * Сохраняет копию внешнего файла во внутреннее хранилище приложения, затем удаляет внешний файл.
     */
    suspend fun deleteExternalWithInternalBackup(fileName: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val dir = getExternalDir() ?: return@runCatching Unit
                val externalFile = File(dir, fileName)
                if (!externalFile.exists() || !externalFile.isFile) {
                    Log.d(logTag, "deleteExternalWithInternalBackup: файл отсутствует $fileName")
                    return@runCatching Unit
                }
                val backupFile = getInternalBackupFile(fileName)
                backupFile.parentFile?.mkdirs()
                externalFile.copyTo(backupFile, overwrite = true)
                externalFile.delete()
                Log.d(logTag, "deleteExternalWithInternalBackup: успех, backup=${backupFile.absolutePath}")
                Unit
            }.onFailure { e ->
                Log.e(logTag, "deleteExternalWithInternalBackup: ошибка fileName=$fileName", e)
            }
        }

    /**
     * Восстанавливает файл из внутренней резервной копии во внешнюю директорию.
     * Имя файла — текущее (из параметра).
     */
    suspend fun restoreExternalFromInternalBackup(fileName: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val backupFile = getInternalBackupFile(fileName)
                if (!backupFile.exists() || !backupFile.isFile) {
                    throw IllegalStateException("Резервная копия отсутствует")
                }
                val dir = getExternalDir()
                    ?: throw IllegalStateException("Внешнее хранилище недоступно")
                val target = File(dir, fileName)
                backupFile.copyTo(target, overwrite = true)
                Log.d(logTag, "restoreExternalFromInternalBackup: успех path=${target.absolutePath}")
                Unit
            }.onFailure { e ->
                Log.e(logTag, "restoreExternalFromInternalBackup: ошибка fileName=$fileName", e)
            }
        }

    /**
     * Информация о внутренней резервной копии.
     */
    suspend fun getInternalBackupInfo(fileName: String): FileInfo = withContext(Dispatchers.IO) {
        val file = getInternalBackupFile(fileName)
        if (!file.exists() || !file.isFile) {
            return@withContext FileInfo(exists = false, fileName = fileName)
        }
        FileInfo(
            exists = true,
            fileName = file.name,
            fullPath = file.absolutePath,
            sizeBytes = file.length(),
            lastModifiedTime = file.lastModified()
        )
    }

    private fun getInternalBackupFile(fileName: String): File {
        val dir = File(context.filesDir, INTERNAL_BACKUP_DIR)
        return File(dir, fileName)
    }

    /** Приоритет: Documents, иначе Downloads. Для Android 8 используем getExternalStoragePublicDirectory. */
    private fun getExternalDir(): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (documents.exists() || documents.mkdirs()) {
                Log.d(logTag, "getExternalDir: Documents=${documents.absolutePath}")
                documents
            } else {
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                Log.d(logTag, "getExternalDir: fallback Downloads=${downloads.absolutePath}")
                downloads
            }
        } else {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            Log.d(logTag, "getExternalDir: Downloads=${downloads.absolutePath}")
            downloads
        }
    }

    private fun formatCharactersAsTxt(characters: List<Character>): String {
        return characters.mapIndexed { index, c ->
            buildString {
                appendLine("Object #${index + 1}")
                appendLine("Name: ${c.name}")
                appendLine("Culture: ${c.culture}")
                appendLine("Born: ${c.born}")
                appendLine("Titles: ${c.titles.joinToString(", ")}")
                appendLine("Aliases: ${c.aliases.joinToString(", ")}")
                appendLine("PlayedBy: ${c.playedBy.joinToString(", ")}")
            }
        }.joinToString("\n")
    }
}

/** Форматирует timestamp в строку даты/времени для отображения. */
fun formatFileLastModified(timeMillis: Long): String {
    if (timeMillis <= 0) return ""
    return try {
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date(timeMillis))
    } catch (_: Exception) {
        ""
    }
}
