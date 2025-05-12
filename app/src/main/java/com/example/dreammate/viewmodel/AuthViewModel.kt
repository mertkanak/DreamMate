package com.example.dreammate.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<Result<FirebaseUser?>>(Result.success(auth.currentUser))
    val authState: StateFlow<Result<FirebaseUser?>> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.emit(Result.runCatching {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                user?.reload()?.await()
                if (user != null && user.isEmailVerified) {
                    user
                } else {
                    throw Exception("E-posta doğrulanmamış. Lütfen e-posta kutunu kontrol et.")
                }
            })
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.emit(Result.runCatching {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.sendEmailVerification()
                throw Exception("Doğrulama e-postası gönderildi. Lütfen gelen kutunu kontrol et.")
            })
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = Result.success(null)
    }
}