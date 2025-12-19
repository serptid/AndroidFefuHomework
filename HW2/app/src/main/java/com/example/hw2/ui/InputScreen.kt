package com.example.hw2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun InputScreen(
    vm: SplitViewModel,
    onCalculate: (String) -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            OutlinedTextField(
                value = vm.total,
                onValueChange = { vm.total = it },
                label = { Text("Total") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = vm.people,
                onValueChange = { vm.people = it },
                label = { Text("People") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = vm.isValid(),
                onClick = { onCalculate(vm.calculate()) }
            ) {
                Text("Calculate")
            }
        }
    }
}
