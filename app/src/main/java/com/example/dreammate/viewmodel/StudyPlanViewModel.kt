package com.example.dreammate.viewmodel

import android.os.Environment
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
    application: android.app.Application
) : AndroidViewModel(application) {

    // PDF çıktısı
    private val _savedPdfFile = MutableStateFlow<File?>(null)
    val savedPdfFile: StateFlow<File?> = _savedPdfFile

    // Yükleniyor durumu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Hata mesajı (UI’da toparlayıp Toast/snackbar’a çevrilebilir)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Seçilen dersler
    private val _selectedSubjects = MutableStateFlow<List<String>>(emptyList())
    val selectedSubjects: StateFlow<List<String>> = _selectedSubjects

    // Seçilen günler
    private val _selectedDays = MutableStateFlow<List<String>>(emptyList())
    val selectedDays: StateFlow<List<String>> = _selectedDays

    /** Ders seçimini toggle’lar (TYT/AYT ayrımını UI’da yapıyoruz) */
    fun toggleSubject(subject: String) {
        val current = _selectedSubjects.value.toMutableList()
        if (current.contains(subject)) current.remove(subject)
        else current.add(subject)
        _selectedSubjects.value = current
    }

    // … StudyPlanViewModel içinde …

    /** Hata mesajını temizler */
    fun clearError() {
        _errorMessage.value = null
    }

    /** Gün seçimini toggle’lar */
    fun toggleDay(day: String) {
        val current = _selectedDays.value.toMutableList()
        if (current.contains(day)) current.remove(day)
        else current.add(day)
        _selectedDays.value = current
    }

    /**
     * OpenAI servisine isteği atar, gelen PDF’i dosyaya yazar,
     * state’leri günceller. UI toast/snackbar göstermeyi
     * errorMessage ve savedPdfFile’dan dinleyebilirsiniz.
     */
    fun generateStudyPlan(
        studentName: String,
        grade: String,
        academicYear: String,
        targetExam: String,
        selectedSubjects: List<SelectedSubject>,
        availableDays: List<String>,
        dailyStudyHours: Float,
        startDate: String,
        examDate: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = StudyPlanRequest(
                    student_name    = studentName,
                    grade           = grade,
                    academic_year   = academicYear,
                    target_exam     = targetExam,
                    selected_subjects = selectedSubjects,
                    available_days  = availableDays,
                    daily_study_hours = dailyStudyHours,
                    start_date      = startDate,
                    exam_date       = examDate
                )

                val response = OpenAIService.api.generateStudyPlan(request)
                if (!response.isSuccessful) {
                    _errorMessage.value = "Hata: ${response.message()}"
                } else {
                    val body = response.body()
                    if (body == null) {
                        _errorMessage.value = "Sunucudan boş cevap geldi."
                    } else {
                        val dir = getApplication<android.app.Application>()
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