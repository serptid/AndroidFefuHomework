# ФИО: Прокопенко Сергей Игоревич
# Группа: Б9123-09.03.01цд
# HW5 - Тестирование Android-приложения "Каталог бесплатных игр"

## Описание проекта

Android-приложение на Kotlin + Jetpack Compose, которое загружает список бесплатных игр через API FreeToGame (https://www.freetogame.com/api), позволяет смотреть детали игры и сохранять игры в избранное (Room).

Стек: Retrofit + OkHttp, Room, Hilt, Coroutines, Flow, Jetpack Compose, Navigation Compose.

## Что добавлено в HW5

### Новые зависимости (app/build.gradle.kts)

```
testImplementation: kotlinx-coroutines-test:1.8.1
testImplementation: app.cash.turbine:turbine:1.1.0

androidTestImplementation: hilt-android-testing:2.52
androidTestImplementation: kotlinx-coroutines-test:1.8.1
androidTestImplementation: app.cash.turbine:turbine:1.1.0
kaptAndroidTest: hilt-compiler:2.52
```

Также изменён testInstrumentationRunner на com.example.hw3.HiltTestRunner.

### Добавленные файлы

```
app/src/test/java/com/example/hw3/
├── MainDispatcherRule.kt
├── FakeGamesRepository.kt
├── GamesViewModelTest.kt        (8 тестов)
└── ModelMappersTest.kt          (3 теста)

app/src/androidTest/java/com/example/hw3/
├── HiltTestRunner.kt
├── FavouritesDaoIntegrationTest.kt   (3 теста)
└── GamesListScreenTest.kt            (3 теста)
```

## Описание тестов

### Unit-тесты

#### MainDispatcherRule.kt
JUnit Rule, который заменяет Dispatchers.Main на StandardTestDispatcher перед каждым тестом и восстанавливает после. Это позволяет тестировать ViewModel без Android-окружения - корутины viewModelScope управляются вручную через advanceUntilIdle().

#### FakeGamesRepository.kt
Ручная реализация интерфейса GamesRepository для изоляции тестов от сети и базы данных:
- gamesResult / gameDetailResult - управляют успехом или ошибкой запросов
- favouritesFlow - MutableStateFlow, имитирует поток из Room
- getGamesCallCount, getDetailCallCount, addFavouriteCallCount, removeFavouriteCallCount - счётчики вызовов для проверки поведения

#### GamesViewModelTest.kt - 8 тестов

| Тест | Что проверяет |
|---|---|
| initialGamesState_isLoading | Начальное состояние ViewModel = Loading |
| loadGames_success_setsSuccessState | Успешный ответ API - состояние Success со списком игр |
| loadGames_error_setsErrorState | IOException - состояние Error с русским текстом "Ошибка сети." |
| onQueryChange_noMatch_setsEmptyState (нетривиальный) | Поиск без совпадений - Empty, а не Success(emptyList()) |
| retryDetail_callsRepositoryAgain (нетривиальный) | retryDetail() делает ещё один запрос: счётчик getDetailCallCount = 2 |
| toggleFavourite_whenAlreadyFavourite_removes | Если игра уже в избранном - вызывается removeFavourite, а не addFavourite |
| favouritesFlow_sequence_emptyThenGame (Flow-тест с Turbine) | Полная последовательность эмиссий - сначала пустой список, потом список с игрой |
| favouritesFlow_noExtraEmissions_onSameState (Flow-тест с Turbine, нетривиальный) | Повторный emit одного и того же списка не вызывает лишней эмиссии (StateFlow конфлейтит одинаковые значения) |

#### ModelMappersTest.kt - 3 теста

| Тест | Что проверяет |
|---|---|
| gameDtoToGame_mapsAllFieldsCorrectly | Все поля GameDto корректно маппятся в Game |
| gameDetailDtoToGameDetail_mapsAllFieldsCorrectly | GameDetailDto с вложенными ScreenshotDto и MinSystemRequirementsDto маппятся правильно |
| toGame_fromEntity_mapsAllFields | FavouriteGameEntity из Room корректно маппится в Game |

### Инструментальные тесты

#### HiltTestRunner.kt
Кастомный AndroidJUnitRunner, который запускает HiltTestApplication вместо обычного Application. Нужен для работы Hilt в инструментальных тестах.

#### FavouritesDaoIntegrationTest.kt - 3 теста (Room, in-memory БД)

| Тест | Что проверяет |
|---|---|
| insert_thenObserveAll_returnsCorrectData | Запись игры - чтение через Flow - данные совпадают |
| doubleInsert_noDuplicate (нетривиальный) | Вставка одной игры дважды с тем же ID (стратегия REPLACE) - в БД ровно 1 запись |
| deleteById_removedFromFlow (Flow-тест с Turbine) | Последовательность эмиссий - пустой список, список с игрой, снова пустой после удаления |

#### GamesListScreenTest.kt - 3 теста (Compose UI)

| Тест | Что проверяет |
|---|---|
| loadingState_showsProgressIndicator | При UiState.Loading на экране есть CircularProgressIndicator |
| successState_showsGameTitles | При UiState.Success заголовок игры отображается в списке |
| errorState_retryButton_invokesCallback (нетривиальный) | Нажатие кнопки "Retry" при UiState.Error вызывает переданный коллбэк |

## Как запустить

### Unit-тесты (без устройства)

Внимание: Windows + кириллица в имени пользователя: если ваш путь к домашней папке содержит кириллицу (например C:\Users\Иван), перед запуском установите переменную окружения:
```powershell
$env:GRADLE_USER_HOME = "C:\gradle-home"
```

```bash
./gradlew test
```

Запустить один класс:
```bash
./gradlew :app:testDebugUnitTest --tests "com.example.hw3.GamesViewModelTest"
```

Отчёт: app/build/reports/tests/testDebugUnitTest/index.html

### Инструментальные тесты (нужен эмулятор или устройство)

```bash
./gradlew connectedAndroidTest
```

Отчёт: app/build/reports/androidTests/connected/index.html

Для запуска через Android Studio: правой кнопкой по папке androidTest - Run Tests.

## Дополнительные улучшения по замечаниям

### Удалён мёртвый код

- ui/components/CommonViews.kt - содержал LoadingView, ErrorView, EmptyView, которые нигде не вызывались
- ui/components/GameCard.kt - компонент карточки, не используемый ни одним экраном

### Декомпозиция GamesViewModel

GamesViewModel тащил на себе сразу все состояния приложения. Разбит на три отдельных класса по принципу одной ответственности:

| Класс | Отвечает за |
|---|---|
| GamesListViewModel | список игр, поиск, pull-to-refresh, набор избранных ID для звёздочек |
| GameDetailViewModel | загрузка деталей игры, retry |
| FavouritesViewModel | список избранного, удаление из избранного |

Каждый экран получает свой ViewModel через hiltViewModel() прямо в AppNavGraph. MainActivity больше не знает ни про какие конкретные VM.

### collectLatest - collect

В observeFavourites() обоих новых VM использовался collectLatest. Внутри лямбды нет тяжёлой отменяемой работы - только присваивание состояния, поэтому заменён на обычный collect.

### Дополнен FavouritesScreen

Раньше состояния Loading и Error обрабатывались через else -> {} - экран просто ничего не показывал. Теперь:
- Loading - CircularProgressIndicator
- Error - текст ошибки

### Вынесен friendlyError

Функция преобразования исключений в читаемый текст перенесена из приватного метода VM в пакетную функцию в UiState.kt, доступную всем VM без дублирования.

## Итог покрытия

| Требование | Количество |
|---|---|
| Unit-тесты | 11 (8 + 3) |
| Интеграционные тесты | 6 (3 + 3) |
| Нетривиальные тесты | 5 |
| Flow-тесты с Turbine (полная последовательность) | 2 |
| Flow-тесты с Turbine (нетривиальное поведение) | 2 |
