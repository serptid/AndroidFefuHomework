# ФИО: Прокопенко Сергей Игоревич
# Группа: Б9123-09.03.01цд
# lab6

В GamesListViewModel реализован реактивный пайплайн, объединяющий три независимых источника данных:

1. MutableStateFlow<String> - поисковый запрос с операторами debounce(350) и distinctUntilChanged
2. MutableSharedFlow<Unit>(replay=1) в связке с flatMapLatest - триггеры первичной загрузки и обновления; при новом событии предыдущий незавершённый запрос отменяется
3. repository.getFavouriteGames().map { Set<Int> }.stateIn - идентификаторы избранных игр из Room

Три источника объединяются через оператор combine в единое состояние GamesScreenState(games, isRefreshing, favouriteIds).
Публичный API ViewModel состоит из двух свойств: val uiState: StateFlow<GamesScreenState> и val searchQuery: StateFlow<String>. В AppNavGraph подписка на них осуществляется через collectAsState().
Параметр replay=1 у SharedFlow необходим для корректной работы тестов на StandardTestDispatcher: он гарантирует, что вызов tryEmit не будет потерян, если корутина stateIn ещё не успела запуститься к моменту эмита.

# lab6_fix

Во всех трёх ViewModel использование mutableStateOf заменено на StateFlow. GamesListViewModel предоставляет uiState и searchQuery, FavouritesViewModel - через stateIn, GameDetailViewModel - через asStateFlow().
Устранён двойной источник правды для поискового запроса: объявление var query by mutableStateOf удалено, компонент TextField читает значение непосредственно из searchQuery.
Метод toggleFavourite больше не обращается к repository.isFavourite(). Проверка выполняется через favouriteIdsFlow.value.contains(id), поскольку данный Flow уже активно собирается в составе общего состояния.

Дополнительные исправления:

- GameDetailScreen явно обрабатывает состояние UiState.Empty
- HTTP-логирование включается только в debug-сборках
- Метод provideFavouriteGamesDao помечен аннотацией @Singleton
- searchQuery экспонируется через asStateFlow()
- FavouritesViewModel использует политику подписки WhileSubscribed(5000)
- Кнопки навигации "назад" переведены на Icons.AutoMirrored.Filled.ArrowBack

# lab5

Подключённые тестовые зависимости: kotlinx-coroutines-test 1.8.1, Turbine 1.1.0, hilt-android-testing 2.52, navigation-testing 2.7.7.

## Unit-тесты (test/)

GamesViewModelTest.kt - 9 тестов, покрывающих:

- начальное состояние ViewModel
- успешную и неуспешную загрузку списка игр
- повторную загрузку через refreshGames при состоянии Success
- работу debounce поискового запроса (проверка через Turbine)
- полную последовательность состояний Error - Loading - Success при вызове retry
- работу toggleFavourite
- корректность последовательностей значений в Flow

ModelMappersTest.kt (класс GamesRepositoryUnitTest) - 4 теста: маппинг DTO в доменные модели, пробрасывание исключений, корректность isFavourite после добавления игры, защита от дублирующихся записей.

## Инструментальные тесты (androidTest/)

GamesRepositoryIntegrationTest.kt - 3 теста, использующих реальную in-memory базу данных Room и FakeFreeToGameApi: добавление игры, обработка дублей, последовательность значений Flow при операциях добавления и удаления.
GamesListScreenTest.kt - 3 теста: отработка состояний Error - Retry - Success с последующим отображением игры, передача корректного идентификатора при клике на элемент списка в NavHost, переход на экран избранного по клику на иконку.

## Изменения в тестах

FavouritesDaoIntegrationTest переработан: вместо прямого тестирования DAO теперь проверяется GamesRepositoryImpl поверх реальной базы Room, что точнее соответствует реальному сценарию использования.
ModelMappersTest заменён на GamesRepositoryUnitTest: тестирование тривиального маппинга полей малоинформативно, поэтому вместо него проверяется поведение репозитория.

## Запуск

Unit-тесты:
./gradlew test
Инструментальные тесты: ./gradlew connectedAndroidTest

Если путь к домашней папке содержит кириллицу, перед запуском установить переменную среды GRADLE_USER_HOME=C:\gradle-home (уже прописано в gradlew.bat).
