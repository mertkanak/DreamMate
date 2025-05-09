package com.example.dreammate.session

import android.content.Context
import com.example.dreammate.data.local.SecureTokenStorage

object AuthTokenHolder {
    var token: String? = null
        private set

    fun init(context: Context) {
        token = SecureTokenStorage.getToken(context)
    }

    fun update(context: Context, newToken: String) {
        token = newToken
        SecureTokenStorage.saveToken(context, newToken)
    }

    fun clear(context: Context) {
        token = null
        SecureTokenStorage.clearToken(context)
    }
}