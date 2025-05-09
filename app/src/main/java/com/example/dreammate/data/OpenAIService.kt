package com.example.dreammate.data

import com.example.dreammate.model.StudyPlanRequest
import com.example.dreammate.service.TokenInterceptor
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
        private const val BASE_URL = "https://stylemate-97o7.onrender.com/"

        val api: OpenAIService by lazy {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                //.proxy(Proxy.NO_PROXY)            // ← proxy bypass
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(TokenInterceptor())   // 🔐 Bearer Token ekleyici
                .addInterceptor(logging)              // 📦 Logcat'e body bas
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIService::class.java)
        }
    }
}