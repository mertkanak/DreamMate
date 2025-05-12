package com.example.dreammate.ui.theme.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetExamSection(
    targetExam: String,
    onExamSelected: (String) -> Unit
) {
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Hedef Sınav", style = MaterialTheme.typography.titleMedium)
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("TYT", "AYT").forEach { exam ->
                    FilterChip(
                        selected = targetExam == exam,
                        onClick = { onExamSelected(exam) },
                        label = { Text(exam) }
                    )
                }
            }
        }
    }
}