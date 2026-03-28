# ФИО: Прокопенко Сергей Игоревич
# Группа: Б9123-09.03.01цд

# HW6 — Flow / Android-приложение «Каталог бесплатных игр»

Стек: Kotlin, Jetpack Compose, Retrofit, Room, Hilt, Coroutines, Flow, Navigation Compose.

## HW6 — Реактивный Flow в GamesListViewModel

Изменён один файл: `GamesListViewModel.kt`.

Было: императивный подход — `var allGames`, ручная отмена через `Job`, `delay(350)`, `observeFavourites()` отдельно.

Стало: реактивный пайплайн из трёх независимых источников, объединённых через `combine`.

### Источники

| # | Тип | Откуда |
|---|---|---|
| 1 | `MutableStateFlow<String>` | поисковый запрос пользователя |
| 2 | `MutableSharedFlow<Unit>` + `flatMapLatest` | триггеры load/refresh → данные из API |
| 3 | `repository.getFavouriteGames()` + `map` | ID избранных игр из Room |

### Операторы

`flatMapLatest` — новый refresh отменяет незавершённый предыдущий запрос.
`debounce(350)` + `distinctUntilChanged` — поиск не срабатывает на каждый символ.
`map` — список `Game` из Room преобразуется в `Set<Int>` ID.
`combine` — три источника сводятся в одно состояние экрана.

### Поведение

Изменение любого из трёх источников пересчитывает `gamesState` без дополнительных вызовов:
- пользователь добавляет игру в избранное — звёздочки обновляются сразу через Room Flow, без перезагрузки списка
- пользователь вводит запрос — debounce 350 мс, затем фильтрация текущего списка
- pull-to-refresh — flatMapLatest отменяет незавершённый запрос и запускает новый

`SharedFlow(replay=1)` для триггера нужен, чтобы `loadGames()` корректно работал в тестах с `StandardTestDispatcher`, где `stateIn`-корутина стартует позже самого вызова `tryEmit`.

## HW5 — Тесты

### Зависимости (app/build.gradle.kts)

```
testImplementation: kotlinx-coroutines-test:1.8.1
testImplementation: app.cash.turbine:turbine:1.1.0
androidTestImplementation: hilt-android-testing:2.52
androidTestImplementation: kotlinx-coroutines-test:1.8.1
androidTestImplementation: app.cash.turbine:turbine:1.1.0
kaptAndroidTest: hilt-compiler:2.52
testInstrumentationRunner: com.example.hw3.HiltTestRunner
```

### Файлы

```
app/src/test/java/com/example/hw3/
    MainDispatcherRule.kt
    FakeGamesRepository.kt
    FakeFreeToGameApi.kt
    FakeFavouriteGamesDao.kt
    GamesViewModelTest.kt          (8 тестов)
    ModelMappersTest.kt            (4 теста, класс GamesRepositoryUnitTest)

app/src/androidTest/java/com/example/hw3/
    HiltTestRunner.kt
    FakeFreeToGameApi.kt
    FavouritesDaoIntegrationTest.kt    (3 теста, класс GamesRepositoryIntegrationTest)
    GamesListScreenTest.kt             (3 теста)
```

### Unit-тесты

`MainDispatcherRule.kt` — JUnit Rule, заменяет `Dispatchers.Main` на `StandardTestDispatcher`. Корутины `viewModelScope` управляются через `advanceUntilIdle()`.

`FakeGamesRepository.kt` — ручная реализация `GamesRepository`. Содержит `gamesResult`, `gameDetailResult`, `favouritesFlow: MutableStateFlow`, счётчики вызовов.

`GamesViewModelTest.kt` — 8 тестов:

| Тест | Что проверяет |
|---|---|
| `initialGamesState_isLoading` | начальное состояние — Loading |
| `loadGames_success_setsSuccessState` | успешный ответ API — Success |
| `loadGames_error_setsErrorState` | IOException — Error с текстом «Ошибка сети.» |
| `onQueryChange_noMatch_setsEmptyState` | пустой результат поиска — Empty, не Success(emptyList()) |
| `retryDetail_callsRepositoryAgain` | retryDetail() делает второй запрос: счётчик = 2 |
| `toggleFavourite_whenAlreadyFavourite_removes` | повторный toggle вызывает removeFavourite |
| `favouritesFlow_sequence_emptyThenGame` | Turbine: полная последовательность эмиссий |
| `favouritesFlow_noExtraEmissions_onSameState` | Turbine: повторный emit того же значения не вызывает лишней эмиссии |

`ModelMappersTest.kt` (класс `GamesRepositoryUnitTest`) — 4 теста бизнес-логики репозитория через `GamesRepositoryImpl` с `FakeFreeToGameApi` и `FakeFavouriteGamesDao`:

| Тест | Что проверяет |
|---|---|
| `getGames_returnsMappedDomainModels` | репозиторий маппит GameDto в Game |
| `getGames_whenApiThrows_propagatesException` | исключение из API пробрасывается наружу |
| `addFavourite_thenIsFavourite_returnsTrue` | isFavourite() = true после addFavourite() |
| `addFavourite_twice_doesNotCreateDuplicate` | повторный addFavourite не создаёт дубль |

### Интеграционные тесты

`FavouritesDaoIntegrationTest.kt` (класс `GamesRepositoryIntegrationTest`) — интеграция `GamesRepositoryImpl` с реальной in-memory Room БД и `FakeFreeToGameApi`. Тестирует слой, через который приложение реально работает с данными, а не DAO напрямую.

| Тест | Что проверяет |
|---|---|
| `addFavourite_getFavouriteGames_returnsCorrectData` | данные читаются корректно через репозиторий |
| `addFavourite_twice_noDuplicateInDatabase` | повторный addFavourite не создаёт дубль в реальной БД |
| `addThenRemoveFavourite_flowEmitsCorrectSequence` | Turbine: пустой список — добавление — удаление |

`GamesListScreenTest.kt` — 3 Compose UI теста через `setContent` без Hilt:

| Тест | Что проверяет |
|---|---|
| `loadingState_showsProgressIndicator` | Loading — виден CircularProgressIndicator |
| `successState_showsGameTitles` | Success — заголовок игры отображается в списке |
| `errorState_retryButton_invokesCallback` | кнопка Retry при Error вызывает коллбэк |

### Итог покрытия

| Требование | Количество |
|---|---|
| Unit-тесты | 12 (8 + 4) |
| Интеграционные тесты | 6 (3 + 3) |
| Нетривиальные тесты | 5 |
| Flow-тесты с Turbine | 4 |

### Исправленные замечания

Было два недостатка в HW5:

1. `FavouritesDaoIntegrationTest` тестировал DAO напрямую, а не репозиторий. Исправлено: тест переписан как `GamesRepositoryIntegrationTest` — используется `GamesRepositoryImpl` с реальной Room БД. Это слой, через который приложение реально работает с данными.

2. `ModelMappersTest` тестировал тривиальный маппинг полей. Исправлено: тест заменён на `GamesRepositoryUnitTest` — проверяется поведение репозитория: маппинг через реальный вызов `getGames()`, пробрасывание исключений, корректность `isFavourite` и защита от дублей.

## Как запустить

Unit-тесты:
```bash
./gradlew test
```

Инструментальные тесты (нужен эмулятор или устройство):
```bash
./gradlew connectedAndroidTest
```

Если путь к домашней папке содержит кириллицу, перед запуском установить переменную:
```powershell
$env:GRADLE_USER_HOME = "C:\gradle-home"
```
