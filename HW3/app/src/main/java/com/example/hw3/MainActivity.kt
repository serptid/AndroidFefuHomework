package com.example.hw3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hw3.ui.AppNavGraph
import com.example.hw3.ui.GamesViewModel
import com.example.hw3.ui.theme.Hw3Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Hw3Theme {
                val vm: GamesViewModel = viewModel()
                AppNavGraph(viewModel = vm)
            }
        }
    }
}
