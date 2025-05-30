package com.example.dreammate.model

data class StudyPlanResponse(
    val weeklyPlan: List<DailyPlan>
)

data class DailyPlan(
    val date: String,
    val totalStudyTimeMinutes: Int,
    val slots: List<StudySlot>
)

data class StudySlot(
    val topic: String,
    val studyTimeMinutes: Int,
    val pastTwoYearQuestions: Int,
    val recommendedQuestions: Int
)