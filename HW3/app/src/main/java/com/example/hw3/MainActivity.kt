package com.example.hw3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.hw3.ui.AppNavGraph
import com.example.hw3.ui.GamesViewModel
import com.example.hw3.ui.theme.Hw3Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Hw3Theme {
                val vm: GamesViewModel = hiltViewModel()
                AppNavGraph(viewModel = vm)
            }
        }
    }
}
