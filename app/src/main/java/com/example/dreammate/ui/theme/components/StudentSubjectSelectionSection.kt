package com.example.dreammate.ui.theme.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ders Seçimi", style = MaterialTheme.typography.titleMedium)

            Row(
                Modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjectList.forEach { subj ->
                    FilterChip(
                        selected = selectedSubjects.contains(subj),
                        onClick = { onSubjectToggle(subj) },
                        label = { Text(subj) }
                    )
                }
            }

            selectedSubjects.forEach { subject ->
                Spacer(modifier = Modifier.height(8.dp))
                Text("$subject Konuları", style = MaterialTheme.typography.titleSmall)

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
                            label = { Text(topic) }
                        )
                    }
                }
            }
        }
    }
}