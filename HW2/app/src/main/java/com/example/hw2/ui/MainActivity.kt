package com.example.hw2.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hw2.ui.theme.HW2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW2Theme {
                val nav = rememberNavController()
                val vm: SplitViewModel = viewModel()

                NavHost(navController = nav, startDestination = "home") {

                    composable("home") {
                        HomeScreen(
                            onStart = { nav.navigate("input") },
                            onHistory = { nav.navigate("history") }
                        )
                    }

                    composable("input") {
                        InputScreen(
                            vm = vm,
                            onCalculate = { id -> nav.navigate("result/$id") }
                        )
                    }

                    composable("history") {
                        HistoryScreen(
                            vm = vm,
                            onOpen = { id -> nav.navigate("result/$id") }
                        )
                    }

                    composable(
                        route = "result/{id}",
                        arguments = listOf(navArgument("id") {
                            type = NavType.Companion.StringType
                        })
                    ) { back ->
                        val id = back.arguments?.getString("id")

                        ResultScreen(
                            vm = vm,
                            id = id,
                            onBackToEdit = { nav.navigate("input") { launchSingleTop = true } },
                            onNew = {
                                vm.reset()
                                nav.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onHistory = { nav.navigate("history") }
                        )
                    }
                }
            }
        }
    }
}