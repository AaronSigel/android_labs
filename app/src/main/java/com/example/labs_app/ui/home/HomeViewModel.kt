package com.example.labs_app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labs_app.data.repository.CharacterRepository
import com.example.labs_app.storage.CharacterListCache
import com.example.labs_app.storage.SharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ViewModel Home. Список берётся из Room (Flow); сеть только синхронизирует БД.
 * Cold start: при отсутствии данных по pageGroup — загрузка с API и сохранение в Room.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: CharacterRepository,
    private val sharedPrefs: SharedPrefsManager
) : ViewModel() {

    private val logTag: String get() = "LabsApp/HomeVM"

    private val _pageGroup = MutableStateFlow(sharedPrefs.getCurrentCharacterPageGroup())

    private val _state = MutableStateFlow(
        HomeUiState(
            pageGroup = _pageGroup.value,
            isLoading = true
        )
    )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        Log.d(logTag, "init: pageGroup=${_pageGroup.value}")
        viewModelScope.launch {
            Log.d(logTag, "init: запуск сбора Flow из Room")
            _pageGroup
                .flatMapLatest { page ->
                    Log.d(logTag, "init: flatMapLatest page=$page, подписка на observeCharactersByPage")
                    repository.observeCharactersByPage(page)
                        .catch { e ->
                            Log.e(logTag, "init: catch Flow error", e)
                            _state.update { it.copy(errorMessage = mapExceptionToMessage(e)) }
                        }
                }
                .collect { list ->
                    Log.d(logTag, "init: collect из Room size=${list.size}")
                    _state.update {
                        it.copy(
                            characters = list,
                            pageGroup = _pageGroup.value,
                            isLoading = it.isLoading,
                            errorMessage = it.errorMessage
                        )
                    }
                    CharacterListCache.set(list)
                }
        }
        viewModelScope.launch {
            Log.d(logTag, "init: запуск collect _pageGroup для ensurePageLoaded")
            _pageGroup.collect { page ->
                Log.d(logTag, "init: _pageGroup emit page=$page → ensurePageLoaded")
                ensurePageLoaded(page)
            }
        }
    }

    /** Cold start / обеспечение данных для страницы: при пустой БД — загрузка с API. */
    private suspend fun ensurePageLoaded(pageGroup: Int) {
        Log.d(logTag, "ensurePageLoaded(pageGroup=$pageGroup)")
        val count = repository.countByPage(pageGroup)
        Log.d(logTag, "ensurePageLoaded: countByPage($pageGroup)=$count")
        if (count > 0) {
            Log.d(logTag, "ensurePageLoaded: данные есть, API не вызываем")
            _state.update { it.copy(isLoading = false, errorMessage = null) }
            return
        }
        Log.d(logTag, "ensurePageLoaded: запрос syncPageFromApi($pageGroup)")
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        repository.syncPageFromApi(pageGroup)
            .fold(
                onSuccess = {
                    Log.d(logTag, "ensurePageLoaded: syncPageFromApi успех")
                    _state.update { it.copy(isLoading = false, errorMessage = null) }
                },
                onFailure = { e ->
                    Log.e(logTag, "ensurePageLoaded: syncPageFromApi ошибка", e)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = mapExceptionToMessage(e)
                        )
                    }
                }
            )
    }

    /** Обновить: сброс на страницу варианта 23 и повторная загрузка с API. */
    fun refresh() {
        Log.d(logTag, "refresh: сброс на pageGroup=$DEFAULT_PAGE_GROUP")
        _pageGroup.value = DEFAULT_PAGE_GROUP
        sharedPrefs.setCurrentCharacterPageGroup(DEFAULT_PAGE_GROUP)
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.syncPageFromApi(DEFAULT_PAGE_GROUP)
                .fold(
                    onSuccess = {
                        Log.d(logTag, "refresh: успех")
                        _state.update { it.copy(isLoading = false, errorMessage = null) }
                    },
                    onFailure = { e ->
                        Log.e(logTag, "refresh: ошибка", e)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = mapExceptionToMessage(e)
                            )
                        }
                    }
                )
        }
    }

    /** Явный запуск загрузки при открытии экрана (cold start). Вызывать из UI в LaunchedEffect(Unit). */
    fun loadIfNeeded() {
        Log.d(logTag, "loadIfNeeded()")
        viewModelScope.launch {
            ensurePageLoaded(_pageGroup.value)
        }
    }

    /** Загрузить ещё: переключение на следующий pageGroup; при отсутствии данных — загрузка с API. */
    fun loadMore() {
        val nextPage = _pageGroup.value + 1
        Log.d(logTag, "loadMore: $nextPage")
        _pageGroup.value = nextPage
        sharedPrefs.setCurrentCharacterPageGroup(nextPage)
        viewModelScope.launch {
            ensurePageLoaded(nextPage)
        }
    }

    fun clearError() {
        Log.d(logTag, "clearError()")
        _state.update { it.copy(errorMessage = null) }
    }

    private fun mapExceptionToMessage(e: Throwable): String = when (e) {
        is UnknownHostException, is ConnectException -> MSG_NO_NETWORK
        is SocketTimeoutException -> MSG_TIMEOUT
        is HttpException -> "Ошибка сервера: ${e.code()} ${e.message().orEmpty()}"
        is IOException -> "Ошибка сети: ${e.message ?: "неизвестная ошибка"}"
        else -> "Ошибка: ${e.message ?: e.javaClass.simpleName}"
    }

    private companion object {
        const val DEFAULT_PAGE_GROUP = 23
        const val MSG_NO_NETWORK = "Нет подключения к интернету"
        const val MSG_TIMEOUT = "Превышено время ожидания ответа от сервера"
    }
}
