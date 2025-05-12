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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Ders Seçimi",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { subj ->
                    val isSelected = selectedSubjects.contains(subj)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onSubjectToggle(subj)
                            if (!isSelected) {
                                subjectTopics.remove(subj)
                            }
                        },
                        label = {
                            Text(
                                text = subj,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            if (selectedSubjects.isNotEmpty()) {
                Text(
                    "Konular",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                selectedSubjects.forEach { subj ->
                    OutlinedTextField(
                        value = subjectTopics[subj] ?: "",
                        onValueChange = { onTopicChange(subj, it) },
                        label = {
                            Text(
                                "$subj Konuları",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        placeholder = {
                            Text(
                                "Virgülle ayır",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
    }
}