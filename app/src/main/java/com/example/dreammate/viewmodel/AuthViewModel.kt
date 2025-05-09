package com.example.dreammate.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // Başlangıçta mevcut kullanıcıyı da burada expose edebiliriz
    private val _authState = MutableStateFlow<Result<FirebaseUser?>>(Result.success(auth.currentUser))
    val authState: StateFlow<Result<FirebaseUser?>> = _authState

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = Result.success(auth.currentUser)
                } else {
                    _authState.value = Result.failure(task.exception ?: Exception("Auth error"))
                }
            }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = Result.success(auth.currentUser)
                } else {
                    _authState.value = Result.failure(task.exception ?: Exception("Auth error"))
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = Result.success(null)
    }
}