@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hw3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.hw3.ui.SettingsState

@Composable
fun SettingsScreen(
    state: SettingsState,
    onBack: () -> Unit,
    onCacheTtlChange: (Int) -> Unit,
    onHistorySizeChange: (Int) -> Unit,
    onThemeChange: (Int) -> Unit,
    onClearCache: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection(title = "Тема оформления") {
                listOf(
                    0 to "Gaming Dark",
                    1 to "AMOLED",
                    2 to "Neon",
                    3 to "Светлая"
                ).forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = state.themeMode == mode,
                                onClick = { onThemeChange(mode) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = state.themeMode == mode, onClick = null)
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            SettingsSection(title = "Время хранения кэша") {
                OutlinedButton(
                    onClick = onClearCache,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Очистить кэш сейчас")
                }
                Spacer(Modifier.height(4.dp))
                listOf(6 to "6 часов", 12 to "12 часов", 24 to "24 часа", 48 to "48 часов")
                    .forEach { (hours, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = state.cacheTtlHours == hours,
                                    onClick = { onCacheTtlChange(hours) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = state.cacheTtlHours == hours, onClick = null)
                            Spacer(Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
            }

            SettingsSection(title = "Размер истории просмотров") {
                listOf(20 to "20 игр", 50 to "50 игр", 100 to "100 игр")
                    .forEach { (size, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = state.historyMaxSize == size,
                                    onClick = { onHistorySizeChange(size) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = state.historyMaxSize == size, onClick = null)
                            Spacer(Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()
        content()
    }
}
