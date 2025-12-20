package com.example.hw2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    vm: SplitViewModel,
    onOpen: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("History", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            vm.history.forEach { item ->
                Text(
                    text = "Per person: ${item.perPerson}",
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onOpen(item.id) }
                )
            }
        }
    }
}
