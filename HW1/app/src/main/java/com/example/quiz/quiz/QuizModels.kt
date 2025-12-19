package com.example.quiz.quiz

data class QuizQuestion(
    val text: String,
    val answers: List<String>,
    val correctIndex: Int
)

sealed interface QuizScreen {
    data object Welcome : QuizScreen
    data object Question : QuizScreen
    data object Result : QuizScreen
}

data class QuizUiState(
    val screen: QuizScreen,
    val questions: List<QuizQuestion>,
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,
    val correctCount: Int = 0
)

sealed interface QuizEvent {
    data object Start : QuizEvent
    data class SelectAnswer(val index: Int) : QuizEvent
    data object Next : QuizEvent
    data object Restart : QuizEvent
}
