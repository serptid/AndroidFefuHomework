package com.example.hw2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    vm: SplitViewModel,
    onSelect: (String) -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text("History", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            vm.history.forEach {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable { onSelect(it.id) }
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("Total: ${it.totalWithTip}")
                        Text("Per person: ${it.perPerson}")
                    }
                }
            }
        }
    }
}
