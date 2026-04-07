# Android. Каталог бесплатных игр

Стек: Kotlin, Jetpack Compose, Retrofit, Room, Hilt, Coroutines, Flow, Navigation Compose.

## lab6

Реактивный пайплайн в GamesListViewModel из трёх независимых источников:

1. MutableStateFlow<String> — поисковый запрос, debounce(350) + distinctUntilChanged
2. MutableSharedFlow<Unit>(replay=1) + flatMapLatest — триггеры загрузки и refresh, отменяет предыдущий незавершённый запрос
3. repository.getFavouriteGames().map { Set<Int> }.stateIn — избранные из Room

Все три объединяются через combine в GamesScreenState(games, isRefreshing, favouriteIds).

Публичный API: val uiState: StateFlow<GamesScreenState> и val searchQuery: StateFlow<String>. AppNavGraph собирает их через collectAsState().

replay=1 у SharedFlow нужен для тестов с StandardTestDispatcher: гарантирует что tryEmit не потеряется до старта stateIn-корутины.

## lab6_fix

Исправления по замечаниям преподавателя.

mutableStateOf заменён на StateFlow во всех трёх VM. GamesListViewModel экспонирует uiState и searchQuery, FavouritesViewModel и GameDetailViewModel — свои StateFlow через stateIn и asStateFlow соответственно.

Двойной источник правды для поискового запроса устранён: var query by mutableStateOf удалён, TextField читает searchQuery напрямую.

toggleFavourite больше не вызывает repository.isFavourite() — проверка через favouriteIdsFlow.value.contains(id).

GameDetailScreen явно обрабатывает UiState.Empty.

HTTP-логирование активно только в debug-сборках. provideFavouriteGamesDao помечен @Singleton. searchQuery экспонируется через asStateFlow(). FavouritesViewModel использует WhileSubscribed(5000). Кнопки назад используют Icons.AutoMirrored.Filled.ArrowBack.

## lab5

Тесты: kotlinx-coroutines-test 1.8.1, Turbine 1.1.0, hilt-android-testing 2.52, navigation-testing 2.7.7.

unit-тесты (test/):
- GamesViewModelTest.kt — 9 тестов: начальное состояние, успех/ошибка загрузки, refreshGames перезагружает при Success, debounce поиска через Turbine, retry проверяет полную последовательность Error-Loading-Success, toggleFavourite, Flow-последовательности
- ModelMappersTest.kt (класс GamesRepositoryUnitTest) — 4 теста: маппинг DTO, пробрасывание исключений, isFavourite после добавления, защита от дублей

инструментальные тесты (androidTest/):
- GamesRepositoryIntegrationTest.kt — 3 теста с реальной in-memory Room БД и FakeFreeToGameApi: добавление, дубли, Flow-последовательность add-remove
- GamesListScreenTest.kt — 3 теста: state-machine Error-Retry-Success показывает игру, NavHost клик по игре передаёт верный ID, NavHost клик по иконке открывает экран избранного

FavouritesDaoIntegrationTest переписан: тестирует GamesRepositoryImpl с реальной Room, а не DAO напрямую.

ModelMappersTest заменён на GamesRepositoryUnitTest: вместо тривиального маппинга полей проверяется поведение репозитория.

## Запуск

Unit-тесты: ./gradlew test

Инструментальные тесты: ./gradlew connectedAndroidTest

Если путь к домашней папке содержит кириллицу, перед запуском установить переменную среды GRADLE_USER_HOME=C:\gradle-home (уже прописано в gradlew.bat).
