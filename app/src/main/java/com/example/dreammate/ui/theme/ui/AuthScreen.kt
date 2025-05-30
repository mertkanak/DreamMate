package com.example.dreammate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dreammate.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import android.util.Log

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoginMode by remember { mutableStateOf(true) }

    LaunchedEffect(authState) {
        authState.fold(
            onSuccess = { user ->
                user?.let {
                    val uid = it.uid
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (!doc.exists()) {
                                // Yeni kullanıcı: öğrenci kayıt
                                val newUser = hashMapOf(
                                    "uid" to uid,
                                    "email" to email,
                                    "role" to "student",
                                    "name" to name,
                                    "grade" to grade,
                                    "school" to school,
                                    "createdAt" to FieldValue.serverTimestamp()
                                )

                                firestore.collection("users").document(uid).set(newUser)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Öğrenci kaydı eklendi")
                                        onAuthenticated()
                                    }
                                    .addOnFailureListener { e ->
                                        errorMsg = "Kayıt eklenirken hata: ${e.localizedMessage}"
                                    }
                            } else {
                                // Mevcut kullanıcı: role kontrolü
                                val role = doc.getString("role")
                                if (role == "teacher") {
                                    errorMsg = "Öğretmen kaydı uygulamadan yapılamaz. Lütfen admin ile iletişime geçin."
                                    FirebaseAuth.getInstance().signOut()
                                } else {
                                    onAuthenticated()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMsg = "Kullanıcı bilgisi alınamadı: ${e.localizedMessage}"
                        }
                }
            },
            onFailure = { error ->
                errorMsg = error.localizedMessage
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Giriş Yap" else "Kayıt Ol (Öğrenci)",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLoginMode) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ad Soyad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = grade,
                onValueChange = { grade = it },
                label = { Text("Sınıf (ör. 11)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = school,
                onValueChange = { school = it },
                label = { Text("Okul") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        errorMsg?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                errorMsg = null
                if (isLoginMode) {
                    viewModel.signIn(email.trim(), password)
                } else {
                    viewModel.signUp(email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Giriş" else "Kayıt Ol")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = {
            isLoginMode = !isLoginMode
            errorMsg = null
        }) {
            Text(if (isLoginMode) "Hesabın yok mu? Kayıt ol" else "Zaten üyeysen giriş yap")
        }
    }
}