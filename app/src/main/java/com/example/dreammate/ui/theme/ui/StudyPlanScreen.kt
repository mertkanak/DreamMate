package com.example.dreammate.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.ExperimentalMaterial3Api
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanScreen(
    viewModel: StudyPlanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // StateFlow'ları doğru şekilde topla
    val isLoading by viewModel.isLoading.collectAsState()
    val pdfFile by viewModel.savedPdfFile.collectAsState()
    val selectedSubjects by viewModel.selectedSubjects.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // UI-only state
    var studentName by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("2024-2025") }
    var targetExam by remember { mutableStateOf("TYT") }
    var startDate by remember { mutableStateOf("") }
    var dailyStudyHours by remember { mutableStateOf("") }
    val subjectTopics = remember { mutableStateMapOf<String, String>() }

    val allDays = listOf("Pzt","Sal","Çar","Per","Cum","Cts","Paz")

    // Hata mesajı gelince Toast göster ve temizle
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError() // istersen bu metodu da ekle ViewModel’e
        }
    }

    Box(modifier.then(modifier).fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isLoading) Modifier.blur(8.dp) else Modifier),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Öğrenci Bilgileri
            item {
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Öğrenci Bilgileri", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = studentName,
                            onValueChange = { studentName = it },
                            label = { Text("Ad Soyad") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Sınıf (örn: 11)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = academicYear,
                            onValueChange = { academicYear = it },
                            label = { Text("Öğretim Yılı") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Hedef Sınav
            item {
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(16.dp)) {
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
                                    onClick = { targetExam = exam },
                                    label = { Text(exam) }
                                )
                            }
                        }
                    }
                }
            }

            // Ders Seçimi & Konular
            item {
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Ders Seçimi", style = MaterialTheme.typography.titleMedium)
                        val subjects = if (targetExam == "TYT")
                            listOf("Mat","Fen","Sos","Türkçe")
                        else
                            listOf("Mat","Fiz","Kim","Bio","Edb","Tar","Cog","Fel","Din")

                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            subjects.forEach { subj ->
                                FilterChip(
                                    selected = selectedSubjects.contains(subj),
                                    onClick = {
                                        viewModel.toggleSubject(subj)
                                        if (!selectedSubjects.contains(subj)) subjectTopics.remove(subj)
                                    },
                                    label = { Text(subj) }
                                )
                            }
                        }

                        if (selectedSubjects.isNotEmpty()) {
                            Text("Konular", style = MaterialTheme.typography.titleSmall)
                            selectedSubjects.forEach { subj ->
                                OutlinedTextField(
                                    value = subjectTopics.getOrDefault(subj, ""),
                                    onValueChange = { subjectTopics[subj] = it },
                                    label = { Text("$subj Konuları") },
                                    placeholder = { Text("Virgülle ayır") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // Gün Seçimi
            item {
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Çalışılacak Günler", style = MaterialTheme.typography.titleMedium)
                        Row(
                            Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allDays.forEach { day ->
                                FilterChip(
                                    selected = selectedDays.contains(day),
                                    onClick = { viewModel.toggleDay(day) },
                                    label = { Text(day) }
                                )
                            }
                        }
                    }
                }
            }

            // Tarih & Saat
            item {
                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Başlangıç Tarihi", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Tarih seç") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val c = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _, y, m, d ->
                                            startDate = "%04d-%02d-%02d".format(y, m + 1, d)
                                        },
                                        c.get(Calendar.YEAR),
                                        c.get(Calendar.MONTH),
                                        c.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                        )

                        OutlinedTextField(
                            value = dailyStudyHours,
                            onValueChange = { dailyStudyHours = it },
                            label = { Text("Günlük Çalışma (saat)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Buton ve PDF Kartı
            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (startDate.isEmpty()
                            || selectedSubjects.isEmpty()
                            || selectedDays.isEmpty()
                            || dailyStudyHours.isBlank()
                        ) {
                            Toast.makeText(context, "Lütfen tüm alanları doldurunuz.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.generateStudyPlan(
                            studentName = studentName,
                            grade = grade,
                            academicYear = academicYear,
                            targetExam = targetExam,
                            selectedSubjects = selectedSubjects.map { sub ->
                                SelectedSubject(
                                    sub,
                                    subjectTopics[sub]
                                        ?.split(",")
                                        ?.map(String::trim)
                                        ?: emptyList()
                                )
                            },
                            availableDays = selectedDays,
                            dailyStudyHours = dailyStudyHours.toFloat(),
                            startDate = startDate,
                            examDate = "2025-06-21"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Planı Oluştur")
                }

                pdfFile?.let { file ->
                    Spacer(Modifier.height(16.dp))
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "✅ Plan Oluşturuldu: ${file.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(12.dp))
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, "application/pdf")
                                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        }
                                    )
                                }) { Text("Görüntüle") }
                                Button(onClick = {
                                    context.startActivity(
                                        Intent.createChooser(
                                            Intent(Intent.ACTION_SEND).apply {
                                                type = "application/pdf"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            },
                                            "Paylaş"
                                        )
                                    )
                                }) { Text("Paylaş") }
                            }
                        }
                    }
                }
            }
        }

        // Yükleniyor Overlay
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xAAFFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}