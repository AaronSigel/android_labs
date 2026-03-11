package com.example.labs_app.data.repository

import android.content.Context
import android.util.Log
import com.example.labs_app.data.local.AppDatabase
import com.example.labs_app.data.local.entity.CharacterEntity
import com.example.labs_app.data.remote.RetrofitProvider
import com.example.labs_app.data.remote.dto.CharacterDto
import com.example.labs_app.domain.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Для варианта 23: pageGroup 23 = ID 1101..1150, pageGroup 24 = 1151..1200 и т.д. */
private const val FIRST_PAGE_GROUP = 23
private const val BASE_START_ID = 1101
private const val PAGE_SIZE = 50

/**
 * Репозиторий: объединяет API и Room. UI читает данные только из Flow Room.
 * Сеть используется только для наполнения/обновления БД.
 */
class CharacterRepository(context: Context) {

    private val logTag: String get() = "LabsApp/CharRepo"

    private val dao = AppDatabase.getInstance(context).characterDao()
    private val api = RetrofitProvider.characterApi

    /**
     * Реактивный поток персонажей по странице. Источник истины для UI.
     */
    fun observeCharactersByPage(pageGroup: Int): Flow<List<Character>> {
        Log.d(logTag, "observeCharactersByPage(pageGroup=$pageGroup)")
        return dao.observeCharactersByPage(pageGroup).map { entities ->
            Log.d(logTag, "observeCharactersByPage($pageGroup): emit size=${entities.size}")
            entities.map { it.toCharacter() }
        }
    }

    /**
     * Синхронное получение списка по странице (для проверки cold start).
     */
    suspend fun getCharactersByPage(pageGroup: Int): List<Character> = withContext(Dispatchers.IO) {
        Log.d(logTag, "getCharactersByPage(pageGroup=$pageGroup)")
        dao.getCharactersByPage(pageGroup).map { it.toCharacter() }.also { list ->
            Log.d(logTag, "getCharactersByPage($pageGroup): size=${list.size}")
        }
    }

    suspend fun countByPage(pageGroup: Int): Int = withContext(Dispatchers.IO) {
        val count = dao.countByPage(pageGroup)
        Log.d(logTag, "countByPage(pageGroup=$pageGroup)=$count")
        count
    }

    /**
     * Загружает с API диапазон для pageGroup и сохраняет в Room по одному.
     * После каждой вставки Room эмитит в Flow — список на экране обновляется в реальном времени.
     */
    suspend fun syncPageFromApi(pageGroup: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val (startId, endId) = pageGroupToIdRange(pageGroup)
        Log.d(logTag, "syncPageFromApi(pageGroup=$pageGroup) ids=$startId..$endId, запись в Room по мере загрузки")
        val now = System.currentTimeMillis()
        var insertedCount = 0
        for (id in startId..endId) {
            runCatching { api.getCharacter(id) }.getOrNull()?.let { dto ->
                val entity = dto.toEntity(id, pageGroup, now)
                runCatching { dao.upsertAll(listOf(entity)) }
                insertedCount++
                Log.d(logTag, "syncPageFromApi($pageGroup): записан id=$id, всего $insertedCount")
            }
        }
        Log.d(logTag, "syncPageFromApi($pageGroup): загружено с API $insertedCount из ${endId - startId + 1}")
        when {
            insertedCount > 0 -> Result.success(Unit)
            else -> {
                Log.w(logTag, "syncPageFromApi($pageGroup): ни одного персонажа не загружено")
                Result.failure(Exception("Не удалось загрузить ни одного персонажа для страницы $pageGroup"))
            }
        }
    }

    /**
     * Очистка всего локального кэша (для настроек).
     */
    suspend fun clearAll(): Result<Unit> = withContext(Dispatchers.IO) {
        Log.d(logTag, "clearAll()")
        runCatching { dao.clearAll() }.also { Log.d(logTag, "clearAll: success=${it.isSuccess}") }
    }

    /**
     * Общее количество записей в БД (для настроек).
     */
    suspend fun getTotalRecordCount(): Int = withContext(Dispatchers.IO) {
        val count = dao.totalCount()
        Log.d(logTag, "getTotalRecordCount()=$count")
        count
    }

    private fun pageGroupToIdRange(pageGroup: Int): Pair<Int, Int> {
        val startId = BASE_START_ID + (pageGroup - FIRST_PAGE_GROUP) * PAGE_SIZE
        return startId to (startId + PAGE_SIZE - 1)
    }

    private fun CharacterEntity.toCharacter(): Character = Character(
        id = id,
        name = name,
        culture = culture,
        born = born,
        titles = titles,
        aliases = aliases,
        playedBy = playedBy
    )

    private fun CharacterDto.toEntity(id: Int, pageGroup: Int, updatedAt: Long): CharacterEntity =
        CharacterEntity(
            id = id,
            name = name,
            culture = culture,
            born = born,
            titles = titles,
            aliases = aliases,
            playedBy = playedBy,
            pageGroup = pageGroup,
            updatedAt = updatedAt
        )
}
