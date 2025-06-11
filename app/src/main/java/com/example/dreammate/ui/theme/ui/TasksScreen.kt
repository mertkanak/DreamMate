package com.example.dreammate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreammate.ui.components.Header
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class TaskItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false
)

@Composable
fun TasksScreen(
    onBackClick: () -> Unit = {} // Navigasyon için parametre (isteğe bağlı)
) {
    var tasks by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    fun toggleTaskCompletion(task: TaskItem) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val taskRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("tasks")
                .document(task.id)

            taskRef.update("isCompleted", !task.isCompleted)
                .addOnSuccessListener {
                    tasks = tasks.map {
                        if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it
                    }
                }
        }
    }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .collection("tasks")
                .get()
                .addOnSuccessListener { result ->
                    tasks = result.documents.map { doc ->
                        TaskItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            isCompleted = doc.getBoolean("isCompleted") ?: false
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    errorMsg = "Görevler yüklenemedi: ${e.localizedMessage}"
                    isLoading = false
                }
        } else {
            errorMsg = "Kullanıcı bulunamadı."
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            title = "Görevler",
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
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tasks) { task ->
                            TaskCard(task = task, onToggleComplete = { toggleTaskCompletion(task) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskItem, onToggleComplete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleComplete() }, // Görev kartına tıklayınca tetiklenir
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(text = task.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}