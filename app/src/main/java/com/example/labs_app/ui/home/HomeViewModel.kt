package com.example.labs_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labs_app.data.remote.repository.CharacterRepository
import com.example.labs_app.domain.model.Character
import com.example.labs_app.storage.CharacterListCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ViewModel экрана Home. Единственный источник состояния загрузки персонажей.
 * При первом открытии экрана запускает загрузку; поддерживает retry.
 */
class HomeViewModel(
    private val repository: CharacterRepository = CharacterRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var loadStarted = false

    /**
     * Запускает загрузку персонажей 1101..1150. Вызывать при входе на экран.
     * Повторные вызовы не запускают бесконечные запросы: загрузка стартует один раз.
     */
    fun loadIfNeeded() {
        if (loadStarted) return
        loadStarted = true
        load()
    }

    /**
     * Повторная загрузка (кнопка «Повторить» при ошибке).
     */
    fun retry() {
        _state.value = HomeUiState.Loading
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = HomeUiState.Loading
            val list = mutableListOf<Character>()
            repository.getCharactersForVariant23Flow()
                .catch { e ->
                    _state.value = HomeUiState.Error(mapExceptionToMessage(e))
                }
                .collect { item ->
                    list.add(item)
                    _state.value = HomeUiState.Success(ArrayList(list), isComplete = false)
                    CharacterListCache.set(ArrayList(list))
                }
            _state.value = if (list.isEmpty()) {
                HomeUiState.Error(MSG_EMPTY_DATA)
            } else {
                HomeUiState.Success(list)
            }
            CharacterListCache.set(list)
        }
    }

    /** Преобразует исключение в понятное сообщение для пользователя. */
    private fun mapExceptionToMessage(e: Throwable): String = when (e) {
        is UnknownHostException, is ConnectException -> MSG_NO_NETWORK
        is SocketTimeoutException -> MSG_TIMEOUT
        is HttpException -> "Ошибка сервера: ${e.code()} ${e.message().orEmpty()}"
        is IOException -> "Ошибка сети: ${e.message ?: "неизвестная ошибка"}"
        else -> "Ошибка: ${e.message ?: e.javaClass.simpleName}"
    }

    private companion object {
        const val MSG_NO_NETWORK = "Нет подключения к интернету"
        const val MSG_TIMEOUT = "Превышено время ожидания ответа от сервера"
        const val MSG_EMPTY_DATA = "Не удалось загрузить ни одного персонажа"
    }
}
