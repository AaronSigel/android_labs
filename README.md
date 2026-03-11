## Lab 7 — Room, кэширование, Flow

### Проверка сценариев

- **Cold start**: Запустить приложение, открыть список (Home). При первом открытии данные загружаются с API и сохраняются в Room; список отображается из БД через Flow. Повторное открытие экрана (без переустановки) — данные берутся из Room без обязательного запроса в сеть.
- **Refresh**: На экране списка нажать «Обновить». Выполняется повторный запрос к API для текущей страницы (pageGroup), Room обновляется, список на экране обновляется реактивно через Flow.
- **Load more**: Нажать «Загрузить ещё». Переключение на следующий pageGroup (23 → 24 → …). Если данные для новой страницы уже есть в БД — показываются сразу; иначе загрузка с API, сохранение в Room, отображение из Flow.

### Зависимости

- **Room** — `androidx.room:room-runtime`, `room-ktx`, компилятор через KSP.

### Структура (ЛР7)

- `data/local/` — CharacterEntity, CharacterDao, AppDatabase, ListConverters
- `data/repository/` — CharacterRepository (API + Room, Flow)
- `ui/home/` — HomeViewModel (cold start / refresh / load more), HomeUiState, HomeScreen (Scaffold, кнопки, индикатор, ошибки)
- Настройки: текущий pageGroup, количество записей в БД, кнопка «Очистить кэш».