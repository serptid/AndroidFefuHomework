package com.example.quiz.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class QuizStateHolder {

    var uiState by mutableStateOf(
        QuizUiState(
            screen = QuizScreen.Welcome,
            questions = defaultQuestions()
        )
    )
        private set

    fun onEvent(event: QuizEvent) {
        when (event) {

            QuizEvent.Start -> {
                uiState = uiState.copy(
                    screen = QuizScreen.Question,
                    currentIndex = 0,
                    selectedIndex = null,
                    correctCount = 0
                )
            }

            is QuizEvent.SelectAnswer -> {
                uiState = uiState.copy(
                    selectedIndex = event.index
                )
            }

            QuizEvent.Next -> {
                val index = uiState.currentIndex
                val selected = uiState.selectedIndex ?: return
                val question = uiState.questions[index]

                val newCorrect =
                    uiState.correctCount +
                            if (selected == question.correctIndex) 1 else 0

                if (index + 1 < uiState.questions.size) {
                    uiState = uiState.copy(
                        currentIndex = index + 1,
                        selectedIndex = null,
                        correctCount = newCorrect
                    )
                } else {
                    uiState = uiState.copy(
                        screen = QuizScreen.Result,
                        correctCount = newCorrect
                    )
                }
            }

            QuizEvent.Restart -> {
                uiState = uiState.copy(
                    screen = QuizScreen.Welcome,
                    currentIndex = 0,
                    selectedIndex = null,
                    correctCount = 0
                )
            }
        }
    }
}

private fun defaultQuestions(): List<QuizQuestion> = listOf(
    QuizQuestion(
        "Как объявить неизменяемую переменную в Kotlin?",
        listOf("var", "val", "let", "const"),
        1
    ),
    QuizQuestion(
        "Какой тип у числа 1 по умолчанию?",
        listOf("Long", "Int", "Short", "Byte"),
        1
    ),
    QuizQuestion(
        "Как правильно написать строковый шаблон?",
        listOf("Hello, {name}", "Hello, \$name", "Hello, %name"),
        1
    ),
    QuizQuestion(
        "Как создать список в Kotlin?",
        listOf("array()", "listOf()", "List()", "new List()"),
        1
    ),
    QuizQuestion(
        "Какой оператор безопасного вызова?",
        listOf("!!", "?:", "?.", "::"),
        2
    ),
    QuizQuestion(
        "Как обозначается nullable-тип?",
        listOf("Int!", "Int?", "?Int", "Nullable<Int>"),
        1
    ),
    QuizQuestion(
        "Как объявить функцию?",
        listOf("function f()", "fun f()", "def f()", "void f()"),
        1
    ),
    QuizQuestion(
        "Что делает оператор ?: ?",
        listOf(
            "Исключение",
            "Безопасный вызов",
            "Значение по умолчанию",
            "Приведение типов"
        ),
        2
    ),
    QuizQuestion(
        "Главный файл Android-приложения?",
        listOf("Start.kt", "MainActivity.kt", "Android.kt", "App.kt"),
        1
    ),
    QuizQuestion(
        "Что такое Jetpack Compose?",
        listOf(
            "XML-разметка",
            "ORM",
            "Декларативный UI-фреймворк",
            "DI-библиотека"
        ),
        2
    )
)
