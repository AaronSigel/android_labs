package com.example.labs_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.labs_app.data.local.converters.ListConverters
import com.example.labs_app.data.local.dao.CharacterDao
import com.example.labs_app.data.local.entity.CharacterEntity

/**
 * Локальная БД Room. Единственный источник истины для списка персонажей на UI.
 */
@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao

    companion object {
        private const val DB_NAME = "labs_app.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build().also { instance = it }
            }
        }
    }
}
