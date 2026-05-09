package com.example.hw3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.hw3.data.local.AppPreferences
import com.example.hw3.ui.AppNavGraph
import com.example.hw3.ui.theme.Hw3Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by appPreferences.themeMode.collectAsState(initial = 0)
            Hw3Theme(themeMode = themeMode) {
                AppNavGraph()
            }
        }
    }
}
