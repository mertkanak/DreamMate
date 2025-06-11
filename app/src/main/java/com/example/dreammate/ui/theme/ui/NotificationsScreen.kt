package com.example.dreammate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreammate.ui.components.Header
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean
)

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {} // Navigasyon için opsiyonel parametre
) {
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("users").document(user.uid)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    notifications = result.documents.map { doc ->
                        val date = doc.getTimestamp("timestamp")?.toDate()
                        val formattedDate = date?.let {
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it)
                        } ?: ""

                        NotificationItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = formattedDate,
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    errorMsg = "Bildirimler yüklenemedi: ${e.localizedMessage}"
                    isLoading = false
                }
        } else {
            errorMsg = "Kullanıcı bulunamadı."
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            title = "Bildirimler",
            showBackButton = true,
            onBackClick = onBackClick
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMsg != null -> {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                notifications.isEmpty() -> {
                    Text(
                        text = "Henüz bir bildirim yok.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationCard(notif)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notif: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.isRead) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notif.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = notif.message, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text(text = notif.timestamp, style = MaterialTheme.typography.labelSmall)
        }
    }
}