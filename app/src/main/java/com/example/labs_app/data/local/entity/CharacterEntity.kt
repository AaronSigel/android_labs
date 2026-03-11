package com.example.labs_app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность Room для персонажа. Хранит данные из API и метаданные кэша.
 */
@Entity(
    tableName = "characters",
    indices = [Index(value = ["pageGroup"])]
)
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val culture: String,
    val born: String,
    val titles: List<String>,
    val aliases: List<String>,
    val playedBy: List<String>,
    /** Номер набора/страницы данных (23, 24, …). */
    val pageGroup: Int,
    /** Время последнего обновления записи. */
    val updatedAt: Long
)
