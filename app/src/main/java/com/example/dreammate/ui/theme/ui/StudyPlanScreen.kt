package com.example.dreammate.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.dreammate.model.SelectedSubject
import com.example.dreammate.viewmodel.StudyPlanViewModel
import java.io.File
import java.util.*

@Composable
fun StudyPlanScreen(
    viewModel: StudyPlanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form verileri
    var studentName by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("2024-2025") }
    var targetExam by remember { mutableStateOf("TYT") }
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var dailyStudyHours by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }

    val subjectTopicsTyt = remember { mutableStateMapOf<String, String>() }
    val subjectTopicsAyt = remember { mutableStateMapOf<String, String>() }
    val selectedSubjectsTyt = remember { mutableStateOf(setOf<String>()) }
    val selectedSubjectsAyt = remember { mutableStateOf(setOf<String>()) }

    val allDays = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar")
    val tytSubjects = listOf("Matematik", "Fen Bilimleri", "Sosyal Bilimler", "Türkçe")
    val aytSubjects = listOf(
        "Matematik", "Fizik", "Kimya", "Biyoloji",
        "Türk Dili ve Edebiyatı", "Tarih", "Coğrafya", "Felsefe", "Din Kültürü"
    )

    val subjectsToShow = if (targetExam == "TYT") tytSubjects else aytSubjects
    val currentSelectedSubjects = if (targetExam == "TYT") selectedSubjectsTyt.value else selectedSubjectsAyt.value
    val currentSubjectsSetter = if (targetExam == "TYT") selectedSubjectsTyt else selectedSubjectsAyt

    val fileName = viewModel.savedPdfFile?.name

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
                .let { if (viewModel.isLoading) it.blur(8.dp) else it }
        ) {
            Text(
                "Çalışma Planı Oluştur",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            // Öğrenci bilgileri
            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Öğrenci Adı Soyadı") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = grade,
                onValueChange = { grade = it },
                label = { Text("Sınıf (örn: 11, 12)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = academicYear,
                onValueChange = { academicYear = it },
                label = { Text("Öğretim Yılı") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Hedef sınav
            Text("Hedef Sınav", style = MaterialTheme.typography.titleMedium)
            DropdownMenuWithItems(
                selected = targetExam,
                items = listOf("TYT", "AYT"),
                onItemSelected = { targetExam = it }
            )
            Spacer(Modifier.height(12.dp))

            // Ders seçimi
            Text("Ders Seçimi", style = MaterialTheme.typography.titleMedium)
            subjectsToShow.forEach { subject ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .toggleable(
                            value = currentSelectedSubjects.contains(subject),
                            onValueChange = { checked ->
                                currentSubjectsSetter.value = if (checked) {
                                    currentSelectedSubjects + subject
                                } else {
                                    currentSelectedSubjects - subject
                                }
                            }
                        )
                ) {
                    Checkbox(
                        checked = currentSelectedSubjects.contains(subject),
                        onCheckedChange = null
                    )
                    Text(subject)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Konu giriş alanları
            if (currentSelectedSubjects.isNotEmpty()) {
                Text("Seçilen Derslerin Konuları", style = MaterialTheme.typography.titleMedium)
                currentSelectedSubjects.forEach { subject ->
                    val map = if (targetExam == "TYT") subjectTopicsTyt else subjectTopicsAyt
                    OutlinedTextField(
                        value = map[subject] ?: "",
                        onValueChange = { map[subject] = it },
                        label = { Text("$subject Konuları (virgül ile ayır)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(12.dp))
            }

            // Çalışılacak gün seçimi
            Text("Çalışabileceğin Günler", style = MaterialTheme.typography.titleMedium)
            allDays.forEach { day ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .toggleable(
                            value = selectedDays.contains(day),
                            onValueChange = { checked ->
                                selectedDays = if (checked) selectedDays + day else selectedDays - day
                            }
                        )
                ) {
                    Checkbox(
                        checked = selectedDays.contains(day),
                        onCheckedChange = null
                    )
                    Text(day)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Başlangıç tarihi seçimi
            Text("Başlangıç Tarihi", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, y, m, d -> startDate = String.format("%04d-%02d-%02d", y, m + 1, d) },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (startDate.isNotEmpty()) startDate else "Tarih Seç")
            }
            Spacer(Modifier.height(8.dp))

            // Günlük çalışma süresi
            OutlinedTextField(
                value = dailyStudyHours,
                onValueChange = { dailyStudyHours = it },
                label = { Text("Günlük Çalışma Süresi (Saat)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(80.dp))
        }

        // Alt kısım: Plan oluştur ve PDF görüntüleme/paylaşma
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                if (startDate.isEmpty()) {
                    Toast.makeText(context, "Lütfen başlangıç ve sınav tarihlerini seçiniz.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val map = if (targetExam == "TYT") subjectTopicsTyt else subjectTopicsAyt
                val subjectsList = (if (targetExam == "TYT") selectedSubjectsTyt.value else selectedSubjectsAyt.value)
                    .map { subj -> SelectedSubject(subj, map[subj]?.split(",")?.map(String::trim) ?: emptyList()) }

                viewModel.generateStudyPlan(
                    studentName = studentName,
                    grade = grade,
                    academicYear = academicYear,
                    targetExam = targetExam,
                    selectedSubjects = subjectsList,
                    availableDays = selectedDays.toList(),
                    dailyStudyHours = dailyStudyHours.toFloatOrNull() ?: 0f,
                    startDate = startDate,
                    examDate = "2025-06-21",
                    context = context
                )
            }) {
                Text("Planı Oluştur", color = Color.White)
            }

            if (viewModel.savedPdfFile != null && fileName != null) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✅ Plan oluşturuldu:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(fileName, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(16.dp))

                        val file = viewModel.savedPdfFile!!
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        Row {
                            Button(onClick = {
                                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                })
                            }) { Text("PDF'i Görüntüle") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }, "Planı Paylaş"))
                            }) { Text("Paylaş") }
                        }
                    }
                }
            }
        }

        if (viewModel.isLoading) {
            Box(
                Modifier.fillMaxSize().background(Color(0xAAFFFFFF)),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
fun DropdownMenuWithItems(
    selected: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) { Text(selected) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { label ->
                DropdownMenuItem(text = { Text(label) }, onClick = {
                    onItemSelected(label)
                    expanded = false
                })
            }
        }
    }
}