package com.example.dreammate.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.dreammate.model.SelectedSubject
import com.example.dreammate.ui.theme.components.*
import com.example.dreammate.viewmodel.StudyPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanScreen(
    viewModel: StudyPlanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val isLoading by viewModel.isLoading.collectAsState()
    val pdfFile by viewModel.savedPdfFile.collectAsState()
    val selectedSubjects by viewModel.selectedSubjects.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()
    val selectedTopics by viewModel.selectedTopics.collectAsState()
    val subjectTopicMap by viewModel.subjectTopicMap.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var studentName by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("2024-2025") }
    var targetExam by remember { mutableStateOf("TYT") }
    var dailyStudyHours by remember { mutableStateOf("") }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Box(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isLoading) Modifier.blur(8.dp) else Modifier),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StudentInfoSection(
                    studentName = studentName,
                    onNameChange = { studentName = it },
                    selectedGrade = viewModel.selectedGrade,
                    gradeOptions = viewModel.gradeOptions,
                    onGradeSelected = viewModel::onGradeSelected,
                    academicYear = academicYear,
                    onAcademicYearChange = { academicYear = it }
                )
            }

            item {
                TargetExamSection(
                    targetExam = targetExam,
                    onExamSelected = { targetExam = it }
                )
            }

            item {
                StudentSubjectSelectionSection(
                    subjectList = subjectTopicMap.keys.toList(),
                    selectedSubjects = selectedSubjects,
                    onSubjectToggle = { viewModel.toggleSubject(it) },
                    subjectTopicMap = subjectTopicMap,
                    selectedTopics = selectedTopics,
                    onTopicToggle = { subject, topic -> viewModel.toggleTopic(subject, topic) }
                )
            }

            item {
                DaySelectionSection(
                    allDays = viewModel.allDays,
                    selectedDays = selectedDays,
                    onToggleDay = { viewModel.toggleDay(it) }
                )
            }

            item {
                OutlinedTextField(
                    value = dailyStudyHours,
                    onValueChange = { dailyStudyHours = it },
                    label = {
                        Text(
                            "Günlük Çalışma (saat)",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            item {
                PlanButtonSection(
                    context = context,
                    isFormValid = selectedSubjects.isNotEmpty()
                            && selectedDays.isNotEmpty()
                            && dailyStudyHours.isNotBlank(),
                    onGeneratePlan = {
                        viewModel.generateStudyPlan(
                            studentName = studentName,
                            grade = viewModel.selectedGrade,
                            academicYear = academicYear,
                            targetExam = targetExam,
                            dailyStudyHours = dailyStudyHours.toFloat(),
                            examDate = "2025-06-21"
                            // startDate gönderilmiyor
                        )
                    },
                    pdfFile = pdfFile
                )
            }
        }

        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}