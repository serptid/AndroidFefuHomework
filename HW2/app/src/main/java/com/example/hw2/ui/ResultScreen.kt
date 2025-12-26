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
import com.example.hw2.domain.SplitResult

@Composable
fun ResultScreen(
    vm: SplitViewModel,
    id: String?,
    onBackToEdit: () -> Unit,
    onNew: () -> Unit,
    onHistory: () -> Unit
) {
    val r = id?.let { vm.getByIdOrNull(it) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (r == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ошибка: результат не найден",
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onBackToEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Back to edit")
                }
            }
        } else {
            ResultContent(
                r = r,
                onBackToEdit = onBackToEdit,
                onNew = onNew,
                onHistory = onHistory
            )
        }
    }
}

@Composable
private fun ResultContent(
    r: SplitResult,
    onBackToEdit: () -> Unit,
    onNew: () -> Unit,
    onHistory: () -> Unit
) {
    val ctx = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text("Tip: ${r.tip}", color = Color.White)
        Text("Total: ${r.totalWithTip}", color = Color.White)
        Text("Per person: ${r.perPerson}", color = Color.White)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onBackToEdit,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) { Text("Back to edit") }

        Spacer(Modifier.height(8.dp))

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
                val text = "Tip: ${r.tip}\nTotal: ${r.totalWithTip}\nPer person: ${r.perPerson}"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                ctx.startActivity(Intent.createChooser(intent, "Share"))
            },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) { Text("Share") }
    }
}
