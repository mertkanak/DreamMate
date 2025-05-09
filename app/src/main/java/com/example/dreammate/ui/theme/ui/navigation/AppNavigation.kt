package com.example.dreammate.ui.theme.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dreammate.session.AuthTokenHolder
import com.example.dreammate.ui.AuthScreen
import com.example.dreammate.ui.StudyPlanScreen
import com.example.dreammate.viewmodel.AuthViewModel
import com.example.dreammate.viewmodel.StudyPlanViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser
    val startDest = if (user != null && user.isEmailVerified) "home" else "auth"
    val context = LocalContext.current

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
                                AuthTokenHolder.update(context, idToken)
                                Log.d("AuthToken", "Token güvenli şekilde alındı ✅")

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
                }
            )
        }

        composable("home") {
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