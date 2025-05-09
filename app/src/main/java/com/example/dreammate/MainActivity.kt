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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreammate.session.AuthTokenHolder
import com.example.dreammate.ui.AuthScreen
import com.example.dreammate.ui.StudyPlanScreen
import com.example.dreammate.ui.theme.DreamMateTheme
import com.example.dreammate.viewmodel.AuthViewModel
import com.example.dreammate.viewmodel.StudyPlanViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // Bu artık sadece fcm için kaldı; auth + studyPlan ViewModel'leri Compose içinde olur
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Android 13+ izin kontrolü (boşta bırakabilirsiniz; auth’dan bağımsız)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // FCM topic + token
        FirebaseMessaging.getInstance().subscribeToTopic("news")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d("FCM", "Topiğe abone olundu: news ✅")
                else Log.e("FCM", "Abonelik başarısız ❌", task.exception)
            }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) Log.d("FCM", "TOKEN: ${task.result}")
            else Log.w("FCM", "Token alınamadı", task.exception)
        }

        setContent {
            DreamMateTheme {
                val navController = rememberNavController()
                // Başlangıç, eğer kullanıcı zaten logged-in ise direkt home, aksi halde auth
                val startDest = if (FirebaseAuth.getInstance().currentUser != null) "home" else "auth"

                NavHost(navController, startDestination = startDest) {
                    composable("auth") {
                        val authVm: AuthViewModel = viewModel()
                        AuthScreen(
                            viewModel = authVm,
                            onAuthenticated = {
                                val user = FirebaseAuth.getInstance().currentUser
                                user?.getIdToken(true)
                                    ?.addOnSuccessListener { result ->
                                        result.token?.let { idToken ->
                                            AuthTokenHolder.token = idToken
                                            Log.d("AuthToken", "Token alındı ve kaydedildi ✅")

                                            // Token alındıktan sonra HOME ekranına geç:
                                            navController.navigate("home") {
                                                popUpTo("auth") { inclusive = true }
                                            }
                                        } ?: run {
                                            Log.e("AuthToken", "Token null geldi ❌")
                                        }
                                    }
                                    ?.addOnFailureListener { e ->
                                        Log.e("AuthToken", "Token alınamadı ❌", e)
                                    }
                                // ↓ Buradaki navigate'i kaldırıyoruz!
                                // navController.navigate("home") { … }
                            }
                        )
                    }

                    composable("home") {
                        // StudyPlanViewModel’i de yine compose içinde al
                        val studyVm: StudyPlanViewModel = viewModel()
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            StudyPlanScreen(
                                viewModel = studyVm,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("FCM", "POST_NOTIFICATIONS izni verildi mi? $isGranted")
    }
}