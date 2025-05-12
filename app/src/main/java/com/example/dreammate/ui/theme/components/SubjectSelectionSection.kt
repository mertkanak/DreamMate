package com.example.dreammate.ui.theme.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSubjectSelectionSection(
    targetExam: String,
    selectedSubjects: List<String>,
    onSubjectToggle: (String) -> Unit,
    subjectTopics: MutableMap<String, String>,
    onTopicChange: (String, String) -> Unit
) {
    val subjects = if (targetExam == "TYT")
        listOf("Mat", "Fen", "Sos", "Türkçe")
    else
        listOf("Mat", "Fiz", "Kim", "Bio", "Edb", "Tar", "Cog", "Fel", "Din")

    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Ders Seçimi", style = MaterialTheme.typography.titleMedium)

            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { subj ->
                    FilterChip(
                        selected = selectedSubjects.contains(subj),
                        onClick = {
                            onSubjectToggle(subj)
                            if (!selectedSubjects.contains(subj)) {
                                subjectTopics.remove(subj)
                            }
                        },
                        label = { Text(subj) }
                    )
                }
            }

            if (selectedSubjects.isNotEmpty()) {
                Text("Konular", style = MaterialTheme.typography.titleSmall)
                selectedSubjects.forEach { subj ->
                    OutlinedTextField(
                        value = subjectTopics[subj] ?: "",
                        onValueChange = { onTopicChange(subj, it) },
                        label = { Text("$subj Konuları") },
                        placeholder = { Text("Virgülle ayır") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}