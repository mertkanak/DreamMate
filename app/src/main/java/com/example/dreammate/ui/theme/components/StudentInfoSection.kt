package com.example.dreammate.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CardDefaults


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoSection(
    studentName: String,
    onNameChange: (String) -> Unit,
    selectedGrade: String,
    gradeOptions: List<String>,
    onGradeSelected: (String) -> Unit,
    academicYear: String,
    onAcademicYearChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Öğrenci Bilgileri", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = studentName,
                onValueChange = onNameChange,
                label = { Text("Ad Soyad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Sınıf Seçimi
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (selectedGrade.isNotBlank()) "$selectedGrade. sınıf" else "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sınıf") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    gradeOptions.forEach { grade ->
                        DropdownMenuItem(
                            text = { Text("$grade. sınıf") },
                            onClick = {
                                onGradeSelected(grade)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = academicYear,
                onValueChange = onAcademicYearChange,
                label = { Text("Öğretim Yılı") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}