package com.example.dreammate.data

import com.example.dreammate.model.StudyPlanRequest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import java.net.Proxy


interface OpenAIService {

    @POST("/generateStudyPlanPdf")
    suspend fun generateStudyPlan(
        @Body request: StudyPlanRequest
    ): Response<ResponseBody>

    companion object {
        private const val BASE_URL = "https://stylemate-97o7.onrender.com/" // ✅

        val api: OpenAIService by lazy {
            // 🔥 OkHttpClient ayarı: Timeout + Logging
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // İsteği ve cevabı Logcat'e bas
            }

            val client = OkHttpClient.Builder()
                // ← proxy bypass
                .connectTimeout(60, TimeUnit.SECONDS) // 🔥 60 saniye bağlantı süresi
                .readTimeout(60, TimeUnit.SECONDS)    // 🔥 60 saniye veri okuma süresi
                .writeTimeout(60, TimeUnit.SECONDS)   // 🔥 60 saniye veri yazma süresi
                .addInterceptor(logging)              // 🔥 Log ekliyoruz
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // 🔥 Buraya client ekliyoruz
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIService::class.java)
        }
    }
}