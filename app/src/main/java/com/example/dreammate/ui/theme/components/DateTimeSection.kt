package com.example.dreammate.ui.theme.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerTextField(
    context: Context,
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }

    // DatePickerDialog tetikleyici
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                onDateSelected(formatter.format(picked.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = modifier.clickable {
            datePickerDialog.show()
        },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Tarih Seç")
        }
    )
}