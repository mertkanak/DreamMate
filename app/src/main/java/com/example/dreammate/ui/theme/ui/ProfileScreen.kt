package com.example.dreammate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreammate.ui.components.Header
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        name = doc.getString("name") ?: ""
                        email = doc.getString("email") ?: ""
                        grade = doc.getString("grade") ?: ""
                        school = doc.getString("school") ?: ""
                    } else {
                        errorMsg = "Profil bilgisi bulunamadı."
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    errorMsg = "Veri alınırken hata: ${e.localizedMessage}"
                    isLoading = false
                }
        } else {
            errorMsg = "Kullanıcı oturumu bulunamadı."
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(
            title = "Profil",
            showBackButton = true,
            onBackClick = onBackClick
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileCard(icon = Icons.Default.Person, label = "Ad Soyad", value = name)
                    ProfileCard(icon = Icons.Default.Email, label = "Email", value = email)
                    ProfileCard(icon = Icons.Default.Star, label = "Sınıf", value = grade)
                    ProfileCard(icon = Icons.Default.LocationOn, label = "Okul", value = school)

                    errorMsg?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            onSignOut()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Çıkış Yap", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(icon: ImageVector, label: String, value: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium)
                Text(text = value.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}