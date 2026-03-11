package com.example.labs_app.storage

/**
 * Информация о файле: наличие, имя, путь, размер, дата изменения.
 * Если файла нет — exists == false, остальные поля могут быть пустыми/нулевыми.
 */
data class FileInfo(
    val exists: Boolean,
    val fileName: String = "",
    val fullPath: String = "",
    val sizeBytes: Long = 0L,
    val lastModifiedTime: Long = 0L
)
