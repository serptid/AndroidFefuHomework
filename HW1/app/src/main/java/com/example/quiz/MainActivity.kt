package com.example.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.example.quiz.quiz.QuizRoot
import com.example.quiz.quiz.QuizStateHolder
import com.example.quiz.ui.theme.QuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                val holder = remember { QuizStateHolder() }

                QuizRoot(
                    state = holder.uiState,
                    onEvent = holder::onEvent
                )
            }
        }
    }
}
