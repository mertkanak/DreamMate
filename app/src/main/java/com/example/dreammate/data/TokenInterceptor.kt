package com.example.dreammate.service

import android.util.Log
import com.example.dreammate.session.AuthTokenHolder
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = AuthTokenHolder.token

        val requestToProceed = if (token != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            // → Burada header’ı loglayalım:
            Log.d("AuthInterceptor", "Authorization header: ${authenticatedRequest.header("Authorization")}")
            authenticatedRequest
        } else {
            Log.d("AuthInterceptor", "No token available, sending original request")
            originalRequest
        }

        return chain.proceed(requestToProceed)
    }
}