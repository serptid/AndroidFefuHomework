package com.example.hw2.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    vm: SplitViewModel,
    id: String,
    onEdit: () -> Unit,
    onNew: () -> Unit
) {
    val r = vm.getById(id)
    val context = LocalContext.current

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Tip amount: ${r.tip}")
            Text("Total with tip: ${r.totalWithTip}")
            Text("Per person: ${r.perPerson}")

            Spacer(Modifier.height(16.dp))

            Button(onClick = onEdit) {
                Text("Back to edit")
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onNew) {
                Text("New calculation")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(onClick = {
                val text =
                    "Tip: ${r.tip}\nTotal: ${r.totalWithTip}\nPer person: ${r.perPerson}"

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(
                    Intent.createChooser(intent, "Share result")
                )
            }) {
                Text("Share")
            }
        }
    }
}
