package com.example.dreammate.model

data class StudyPlanRequest(
    val student_name: String,
    val grade: String,
    val academic_year: String,
    val target_exam: String, // "TYT", "AYT", "YDT" gibi
    val selected_subjects: List<SelectedSubject>,  // 🔥 Ders ve Konular birlikte!
    val available_days: List<String>,
    val daily_study_hours: Float,
    val exam_date: String = "2025-06-21", // Sabit sınav tarihi
    val start_date: String? = null                 // Öğretmenin seçtiği başlangıç tarihi (DatePicker'dan)
)

data class SelectedSubject(
    val subject: String,
    val topics: List<String>
)