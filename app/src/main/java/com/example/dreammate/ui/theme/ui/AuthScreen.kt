package com.example.dreammate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dreammate.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseUser

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ViewModel’den gelen auth sonucu
    val authState by viewModel.authState.collectAsState()

    // UI state’leri
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Auth değiştiğinde, başarılıysa onAuthenticated tetiklenir
    LaunchedEffect(authState) {
        authState.fold(
            onSuccess = { user ->
                if (user != null) onAuthenticated()
            },
            onFailure = { error ->
                errorMsg = error.localizedMessage
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Giriş Yap" else "Kayıt Ol",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        errorMsg?.let {
            Spacer(Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                errorMsg = null
                if (isLoginMode) viewModel.signIn(email.trim(), password)
                else viewModel.signUp(email.trim(), password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Giriş" else "Kayıt Ol")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            isLoginMode = !isLoginMode
            errorMsg = null
        }) {
            Text(
                if (isLoginMode)
                    "Hesabın yok mu? Kayıt ol"
                else
                    "Zaten üyeysen giriş yap"
            )
        }
    }
}