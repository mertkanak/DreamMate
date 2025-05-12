package com.example.dreammate.ui.theme.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StudentSubjectSelectionSection(
    subjectList: List<String>, // Backend’den gelen ders listesi
    selectedSubjects: List<String>,
    onSubjectToggle: (String) -> Unit,
    subjectTopicMap: Map<String, List<String>>, // Her dersin konuları
    selectedTopics: Map<String, List<String>>,  // Kullanıcının seçtiği konular
    onTopicToggle: (subject: String, topic: String) -> Unit
) {
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
                text = "Ders Seçimi",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjectList.forEach { subj ->
                    FilterChip(
                        selected = selectedSubjects.contains(subj),
                        onClick = { onSubjectToggle(subj) },
                        label = {
                            Text(
                                text = subj,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedSubjects.contains(subj))
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

            selectedSubjects.forEach { subject ->
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$subject Konuları",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val topics = subjectTopicMap[subject].orEmpty()
                val selected = selectedTopics[subject].orEmpty()

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    topics.forEach { topic ->
                        FilterChip(
                            selected = selected.contains(topic),
                            onClick = { onTopicToggle(subject, topic) },
                            label = {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected.contains(topic))
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
            }
        }
    }
}