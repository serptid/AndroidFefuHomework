package com.example.hw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.hw2.ui.*
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
                        InputScreen(vm) { id ->
                            nav.navigate("result/$id")
                        }
                    }

                    composable("history") {
                        HistoryScreen(
                            vm = vm,
                            onSelect = { id ->
                                nav.navigate("result/$id")
                            }
                        )
                    }

                    composable(
                        "result/{id}",
                        arguments = listOf(navArgument("id") {
                            type = NavType.StringType
                        })
                    ) { backStack ->
                        val id = backStack.arguments!!.getString("id")!!

                        ResultScreen(
                            vm = vm,
                            id = id,
                            onEdit = { nav.popBackStack() },
                            onNew = {
                                vm.reset()
                                nav.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
