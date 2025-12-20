package com.example.hw2.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    vm: SplitViewModel,
    id: String,
    onNew: () -> Unit,
    onHistory: () -> Unit
) {
    val r = vm.getById(id)
    val ctx = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Tip: ${r.tip}", color = Color.White)
            Text("Total: ${r.totalWithTip}", color = Color.White)
            Text("Per person: ${r.perPerson}", color = Color.White)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onNew,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) { Text("New") }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onHistory,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) { Text("History") }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = {
                    val text =
                        "Tip: ${r.tip}\nTotal: ${r.totalWithTip}\nPer person: ${r.perPerson}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    ctx.startActivity(Intent.createChooser(intent, "Share"))
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Share")
            }
        }
    }
}
