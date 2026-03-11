package com.example.labs_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.labs_app.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для персонажей: CRUD и наблюдение за списком по странице.
 */
@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters WHERE pageGroup = :pageGroup ORDER BY id ASC")
    fun observeCharactersByPage(pageGroup: Int): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE pageGroup = :pageGroup ORDER BY id ASC")
    suspend fun getCharactersByPage(pageGroup: Int): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CharacterEntity>)

    @Query("DELETE FROM characters WHERE pageGroup = :pageGroup")
    suspend fun deleteByPage(pageGroup: Int)

    @Query("DELETE FROM characters")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM characters WHERE pageGroup = :pageGroup")
    suspend fun countByPage(pageGroup: Int): Int

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun totalCount(): Int
}
