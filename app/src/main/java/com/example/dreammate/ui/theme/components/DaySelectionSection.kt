package com.example.dreammate.ui.theme.components

import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelectionSection(
    allDays: List<String>,
    selectedDays: List<String>,
    onToggleDay: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Çalışılacak Günler", style = MaterialTheme.typography.titleMedium)

            // Dropdown Spinner tarzı
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = if (selectedDays.isNotEmpty()) selectedDays.joinToString(", ") else "Gün seçin",
                    onValueChange = {},
                    label = { Text("Seçilen Günler") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    maxLines = 1,
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    allDays.forEach { day ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(day)
                                    if (selectedDays.contains(day)) {
                                        Text("✓")
                                    }
                                }
                            },
                            onClick = {
                                onToggleDay(day)
                            }
                        )
                    }
                }
            }
        }
    }
}