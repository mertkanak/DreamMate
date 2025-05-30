package com.example.dreammate.viewmodel

import android.app.Application
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreammate.data.OpenAIService
import com.example.dreammate.model.SelectedSubject
import com.example.dreammate.model.StudyPlanRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class StudyPlanViewModel(
    application: Application
) : AndroidViewModel(application) {

    // ---------------- Genel UI Stateleri ----------------

    var selectedGrade by mutableStateOf("")
        private set

    var startDate by mutableStateOf("")
        private set

    fun onGradeSelected(grade: String) {
        selectedGrade = grade
    }

    fun onStartDateSelected(date: String) {
        startDate = date
    }

    // ---------------- Sabit Listeler ----------------

    private val _gradeOptions = listOf("9", "10", "11", "12")
    val gradeOptions: List<String> = _gradeOptions

    private val _allDays = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cts", "Paz")
    val allDays: List<String> = _allDays

    // ---------------- Dersler ve Konular ----------------

    private val _selectedSubjects = MutableStateFlow<List<String>>(emptyList())
    val selectedSubjects: StateFlow<List<String>> = _selectedSubjects

    private val _selectedTopics = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val selectedTopics: StateFlow<Map<String, List<String>>> = _selectedTopics

    private val _subjectTopicMap = MutableStateFlow<Map<String, List<String>>>(
        mapOf(
            "Matematik" to listOf("Türev", "İntegral", "Limit"),
            "Fizik" to listOf("Kuvvet", "Hareket", "Enerji"),
            "Kimya" to listOf("Maddeler", "Asit Baz", "Mol"),
            "Biyoloji" to listOf("Hücre", "Kalıtım", "Ekosistem")
        )
    )
    val subjectTopicMap: StateFlow<Map<String, List<String>>> = _subjectTopicMap

    fun toggleSubject(subject: String) {
        val current = _selectedSubjects.value.toMutableList()
        if (current.contains(subject)) current.remove(subject) else current.add(subject)
        _selectedSubjects.value = current
    }

    fun toggleTopic(subject: String, topic: String) {
        val current = _selectedTopics.value.toMutableMap()
        val list = current[subject]?.toMutableList() ?: mutableListOf()

        if (list.contains(topic)) list.remove(topic) else list.add(topic)
        current[subject] = list
        _selectedTopics.value = current
    }

    // ---------------- Gün Seçimi ----------------

    private val _selectedDays = MutableStateFlow<List<String>>(emptyList())
    val selectedDays: StateFlow<List<String>> = _selectedDays

    fun toggleDay(day: String) {
        val current = _selectedDays.value.toMutableList()
        if (current.contains(day)) current.remove(day) else current.add(day)
        _selectedDays.value = current
    }

    // ---------------- Yüklenme / Hata / Dosya ----------------

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
    }

    private val _savedPdfFile = MutableStateFlow<File?>(null)
    val savedPdfFile: StateFlow<File?> = _savedPdfFile

    // ---------------- Plan Oluşturma ----------------

    fun generateStudyPlan(
        studentName: String,
        grade: String,
        academicYear: String,
        targetExam: String,
        dailyStudyHours: Float,
        examDate: String,
        startDate: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = StudyPlanRequest(
                    student_name = studentName,
                    grade = grade,
                    academic_year = academicYear,
                    target_exam = targetExam,
                    selected_subjects = _selectedSubjects.value.map { subject ->
                        SelectedSubject(
                            subject,
                            _selectedTopics.value[subject] ?: emptyList()
                        )
                    },
                    available_days = _selectedDays.value,
                    daily_study_hours = dailyStudyHours,
                    start_date = startDate,
                    exam_date = examDate
                )

                val response = OpenAIService.api.generateStudyPlan(request)
                if (!response.isSuccessful) {
                    _errorMessage.value = "Hata: ${response.message()}"
                } else {
                    val body = response.body()
                    if (body == null) {
                        _errorMessage.value = "Sunucudan boş cevap geldi."
                    } else {
                        val dir = getApplication<Application>()
                            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        val fileName = "study_plan_${System.currentTimeMillis()}.pdf"
                        val file = File(dir, fileName)

                        file.outputStream().use { output ->
                            body.byteStream().copyTo(output)
                        }

                        _savedPdfFile.value = file
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "İstisna: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}