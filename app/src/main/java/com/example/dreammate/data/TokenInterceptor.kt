package com.example.dreammate.service

import android.util.Log
import com.example.dreammate.session.AuthTokenHolder
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = AuthTokenHolder.token

        return if (!token.isNullOrBlank()) {
            val authenticatedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            // Token içeriği loglanmaz, sadece header var mı diye log yazılabilir (opsiyonel):
            Log.d("TokenInterceptor", "Authorization header eklendi ✅")
            chain.proceed(authenticatedRequest)
        } else {
            Log.w("TokenInterceptor", "Token yok, header eklenmeden istek gönderiliyor ⚠️")
            chain.proceed(originalRequest)
        }
    }
}