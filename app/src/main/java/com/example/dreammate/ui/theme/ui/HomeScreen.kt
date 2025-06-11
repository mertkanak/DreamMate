package com.example.dreammate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.dreammate.ui.components.Header


import com.example.dreammate.ui.components.Header

@Composable
fun HomeScreen(
    onNavigateToStudyPlan: () -> Unit,
    onNavigateToTest: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    var userName by remember { mutableStateOf("...") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("name") ?: "Kullanıcı"
                    isLoading = false
                }
                .addOnFailureListener {
                    userName = "Kullanıcı"
                    isLoading = false
                }
        } else {
            userName = "Kullanıcı"
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header Component'i ekliyoruz
            Header(
                title = "Ana Sayfa",
                actionContent = {
                    IconButton(onClick = { /* Bildirim ekranına git */ }) {
                        Icon(Icons.Outlined.EmojiEvents, contentDescription = "Bildirimler")
                    }
                }
            )

            // Ana içerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Merhaba, $userName! 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Bugünün hedefi: 3 konu, 50 soru",
                    style = MaterialTheme.typography.bodyMedium
                )

                HomeCard(
                    icon = Icons.Outlined.DateRange,
                    text = "Çalışma Planı Oluştur",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onNavigateToStudyPlan
                )

                HomeCard(
                    icon = Icons.Outlined.Quiz,
                    text = "Günlük Test Çöz",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onNavigateToTest
                )

                HomeCard(
                    icon = Icons.Outlined.BarChart,
                    text = "İstatistiklerim",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = onNavigateToStats
                )

                HomeCard(
                    icon = Icons.Outlined.EmojiEvents,
                    text = "Hedeflerim",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = { /* Hedeflerim ekranına yönlendir */ }
                )

                Spacer(Modifier.height(24.dp))
                Divider()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "\"Başlamak için mükemmel olmayı bekleme, mükemmel olmak için başla.\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text, fontSize = 16.sp)
        }
    }
}