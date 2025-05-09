package com.example.dreammate.service

import android.util.Log
import com.example.dreammate.session.AuthTokenHolder
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        // 1) URL’i logla
        Log.d("AuthInterceptor", "Requesting URL → ${original.url}")
        val token = AuthTokenHolder.token
        return if (token != null) {
            val req = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            // 2) Header’ı logla
            Log.d("AuthInterceptor", "Authorization header → ${req.header("Authorization")}")
            chain.proceed(req)
        } else {
            Log.d("AuthInterceptor", "No token, proceeding without auth header")
            chain.proceed(original)
        }
    }
}