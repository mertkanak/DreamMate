package com.example.dreammate.ui.theme.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun PlanButtonSection(
    context: Context,
    isFormValid: Boolean,
    onGeneratePlan: () -> Unit,
    pdfFile: File?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                if (!isFormValid) {
                    Toast.makeText(context, "Lütfen tüm alanları doldurunuz.", Toast.LENGTH_SHORT).show()
                } else {
                    onGeneratePlan()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Planı Oluştur")
        }

        pdfFile?.let { file ->
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("✅ Plan Oluşturuldu: ${file.name}")

                    val uri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                            )
                        }) {
                            Text("Görüntüle")
                        }

                        Button(onClick = {
                            context.startActivity(
                                Intent.createChooser(
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    },
                                    "Paylaş"
                                )
                            )
                        }) {
                            Text("Paylaş")
                        }
                    }
                }
            }
        }
    }
}