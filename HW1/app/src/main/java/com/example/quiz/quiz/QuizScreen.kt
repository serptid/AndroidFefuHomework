package com.example.quiz.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QuizRoot(state: QuizUiState, onEvent: (QuizEvent) -> Unit) {
    when (state.screen) {
        QuizScreen.Welcome ->
            WelcomeScreen { onEvent(QuizEvent.Start) }

        QuizScreen.Question -> {
            val q = state.questions[state.currentIndex]
            QuestionScreen(
                index = state.currentIndex + 1,
                total = state.questions.size,
                question = q,
                selectedIndex = state.selectedIndex,
                onSelect = { onEvent(QuizEvent.SelectAnswer(it)) },
                onNext = { onEvent(QuizEvent.Next) }
            )
        }

        QuizScreen.Result ->
            ResultScreen(
                correct = state.correctCount,
                total = state.questions.size,
                onRestart = { onEvent(QuizEvent.Restart) }
            )
    }
}

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Quiz Trainer",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("Начать")
        }
    }
}

@Composable
private fun QuestionScreen(
    index: Int,
    total: Int,
    question: QuizQuestion,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

        Text(
            "Вопрос $index из $total",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            question.text,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        question.answers.forEachIndexed { i, ans ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(i) }
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    RadioButton(
                        selected = selectedIndex == i,
                        onClick = { onSelect(i) }
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(ans)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onNext,
            enabled = selectedIndex != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Дальше")
        }
    }
}

@Composable
private fun ResultScreen(correct: Int, total: Int, onRestart: () -> Unit) {
    val percent = correct * 100 / total

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Результат: $correct из $total ($percent%)",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
            Text("Пройти ещё раз")
        }
    }
}
