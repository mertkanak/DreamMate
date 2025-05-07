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
import com.example.dreammate.ui.StudyPlanScreen
import com.example.dreammate.ui.theme.DreamMateTheme
import com.example.dreammate.viewmodel.StudyPlanViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val viewModel: StudyPlanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // ✅ Android 13+ için bildirim iznini iste
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Topiğe abone olundu: news ✅")
                } else {
                    Log.e("FCM", "Abonelik başarısız ❌", task.exception)
                }
            }

        // ✅ FCM token’ı al ve logla
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "TOKEN: $token")
            } else {
                Log.w("FCM", "Token alınamadı", task.exception)
            }
        }

        // ✅ UI başlat
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

    // 🔹 Android 13+ için izin sonucu işleyici
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("FCM", "POST_NOTIFICATIONS izni verildi mi? $isGranted")
    }
}