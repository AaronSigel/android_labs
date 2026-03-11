package com.example.labs_app.data.local.converters

import androidx.room.TypeConverter

/**
 * Конвертеры Room для списков строк (titles, aliases, playedBy).
 * Хранение в виде строки с разделителем (элементы не должны содержать разделитель).
 */
class ListConverters {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(DELIMITER)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split(DELIMITER)
    }

    private companion object {
        private const val DELIMITER = "|||"
    }
}
