package com.example.dreammate.viewmodel

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dreammate.data.OpenAIService
import com.example.dreammate.model.SelectedSubject
import com.example.dreammate.model.StudyPlanRequest
import kotlinx.coroutines.launch
import java.io.File

class StudyPlanViewModel : ViewModel() {

    var savedPdfFile by mutableStateOf<File?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun generateStudyPlan(
        studentName: String,
        grade: String,
        academicYear: String,
        targetExam: String,
        selectedSubjects: List<SelectedSubject>,
        availableDays: List<String>,
        dailyStudyHours: Float,
        startDate: String,
        examDate: String,
        context: Context
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = StudyPlanRequest(
                    student_name = studentName,
                    grade = grade,
                    academic_year = academicYear,
                    target_exam = targetExam,
                    selected_subjects = selectedSubjects,
                    available_days = availableDays,
                    daily_study_hours = dailyStudyHours,
                    start_date = startDate,
                    exam_date = examDate
                )

                val response = OpenAIService.api.generateStudyPlan(request)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        val fileName = "study_plan_${System.currentTimeMillis()}.pdf"
                        val file = File(dir, fileName)

                        file.outputStream().use { output ->
                            body.byteStream().copyTo(output)
                        }

                        savedPdfFile = file

                        Toast.makeText(context, "PDF başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        errorMessage = "Sunucudan boş cevap geldi."
                        Toast.makeText(context, "Sunucudan boş cevap geldi.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    errorMessage = "Hata: ${response.message()}"
                    Toast.makeText(context, "Hata oluştu: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "İstisna: ${e.localizedMessage}"
                Toast.makeText(context, "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }
}