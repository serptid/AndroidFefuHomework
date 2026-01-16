### ФИО: Прокопенко Сергей Игоревич
### Группа: Б9123-09.03.01цд

# Используемый API
Описание:
Публичный REST API, предоставляющий информацию о бесплатных free-to-play и MMO-играх: название, жанр, платформу, дату релиза, описание и другие метаданные.
API не требует ключа доступа.

Base URL: `https://www.freetogame.com/api`

# Функциональность приложения

* Загрузка списка free-to-play игр
* Поиск по названию (с debounce)
* Просмотр детальной информации об игре
* Добавление и удаление игр из избранного
* Обновление списка жестом (pull-to-refresh)
* Обработка состояний загрузки и ошибок
* Кэширование данных в памяти (ViewModel)

# Скриншоты
![photo_4_2026-01-13_16-43-41](https://github.com/user-attachments/assets/7e635bcf-92ba-4783-8454-5a1f1135d089)
Loading — состояние загрузки
![photo_2_2026-01-13_16-43-41](https://github.com/user-attachments/assets/cb19c456-da73-4437-b8b2-aed66f67f81f)
Error — ошибка сети + Retry
![photo_3_2026-01-13_16-43-41](https://github.com/user-attachments/assets/fa516025-d2a7-4b6e-8d07-41de635f07e1)
List — список игр
![photo_5_2026-01-13_16-43-41](https://github.com/user-attachments/assets/65fd37a3-4003-422a-b36a-3987790208b6)
Detail — экран деталей игры
![photo_1_2026-01-13_16-43-41](https://github.com/user-attachments/assets/1e80b79b-1c96-4c81-b807-8392fed48aa6)
Favourites — экран избранного

# Архитектура
UI (Compose)
   ⇒
ViewModel (state, логика, viewModelScope)
   ⇒
Repository
   ⇒
Retrofit + OkHttp
   ⇒
REST API

# Чеклист требований
## Обязательные
- [x] **Jetpack Compose + Material3**
- [x] **Navigation Compose** (List / Detail / Favourites)
- [x] **ViewModel + viewModelScope**
- [x] **Retrofit + Gson**
- [x] **UI состояния:** Loading / Error / Empty / Success
- [x] **Избранное** (локально, без БД)

## Бонусы
- [x] **Debounce поиска без Flow** (Job + delay)
- [x] **Pull-to-refresh**
- [x] **Кэш данных в памяти**
- [x] **Логирование сетевых запросов** (OkHttp Logging Interceptor)
- [x] **Улучшенная обработка сетевых ошибок**

