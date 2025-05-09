package com.example.dreammate

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.dreammate.session.AuthTokenHolder
import com.example.dreammate.ui.StudyPlanScreen
import com.example.dreammate.ui.theme.DreamMateTheme
import com.example.dreammate.viewmodel.StudyPlanViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val viewModel: StudyPlanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 🔐 Kullanıcı login olduysa FCM işlemlerini yap
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // 1. FCM Topiğe abone ol
            FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "Topiğe abone olundu: news ✅")
                    } else {
                        Log.e("FCM", "Abonelik başarısız ❌", task.exception)
                    }
                }

            // 2. FCM token’ı al ve sunucuya gönder (loglama yok!)
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { fcmToken ->
                    sendFcmTokenToBackend(fcmToken)
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Token alınamadı", e)
                }
        }

        setContent {
            DreamMateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudyPlanScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun sendFcmTokenToBackend(token: String) {
        val jwt = AuthTokenHolder.token
        if (!jwt.isNullOrBlank()) {
            // 🔐 Burada Retrofit veya başka servisle token'ı backend'e gönder
            Log.d("FCM", "FCM token sunucuya gönderiliyor... (JWT ile)")
            // Örnek: api.sendFcmToken("Bearer $jwt", token)
        } else {
            Log.w("FCM", "JWT token yok, FCM token gönderilemedi ❌")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("FCM", "POST_NOTIFICATIONS izni verildi mi? $isGranted")
    }
}