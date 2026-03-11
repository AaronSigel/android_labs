# Labs APP

## Lab 5 — сетевая загрузка (вариант 23)

### Добавленные зависимости

- **Kotlin Serialization** — плагин `org.jetbrains.kotlin.plugin.serialization`, библиотека `kotlinx-serialization-json`
- **Retrofit** — `retrofit`, `converter-kotlinx-serialization`
- **OkHttp** — `okhttp`
- **Lifecycle** — `lifecycle-viewmodel-ktx`, `lifecycle-runtime-compose`

### API

Используется **Game of Thrones API** (An API of Ice and Fire):

- Документация: https://anapioficeandfire.com/
- Базовый URL: `https://anapioficeandfire.com/`
- Эндпоинт персонажа: `GET /api/characters/{id}`

### Диапазон ID (вариант 23)

Загружаются персонажи с ID от **1101 до 1150** включительно (50 запросов).

### Воспроизведение ошибки сети

1. **Нет интернета**: включите режим полёта на устройстве/эмуляторе и откройте экран Home (Чаты) после входа.
2. **Таймаут**: при очень медленной сети запросы могут завершиться по таймауту (30 с); для проверки можно временно уменьшить таймаут в `CharacterRepository` или отключить сеть во время загрузки.
3. **Ошибка после загрузки**: загрузите экран, затем включите режим полёта и нажмите «Повторить».

На экране появится сообщение об ошибке и кнопка «Повторить».
