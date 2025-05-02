package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.MonthlyGoal
import kotlinx.coroutines.launch
import java.util.*
import java.time.Month


@Composable
fun AddMonthlyGoalDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.monthlyGoalDao()
    val scope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var min by remember { mutableStateOf("") }
    var max by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val monthId = "%04d-%02d".format(selectedYear, selectedMonth + 1)
                val minVal = min.toFloatOrNull() ?: 0f
                val maxVal = max.toFloatOrNull() ?: 0f

                if (minVal >= 0f && maxVal >= minVal) {
                    scope.launch {
                        dao.insert(
                            MonthlyGoal(
                                monthId = monthId,
                                minAmount = minVal,
                                maxAmount = maxVal
                            )
                        )
                        onDismiss()
                    }
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Monthly Goal") },

        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GoalDropdownSelector(
                        label = "Month",
                        value = Month.of(selectedMonth + 1).name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        onValueSelected = { index -> selectedMonth = index },
                        options = Month.values().map {
                            it.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    GoalDropdownSelector(
                        label = "Year",
                        value = "$selectedYear",
                        onValueSelected = { index ->
                            selectedYear = calendar.get(Calendar.YEAR) - 2 + index
                        },
                        options = List(5) { calendar.get(Calendar.YEAR) - 2 + it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = min,
                    onValueChange = { min = it },
                    label = { Text("Min Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = max,
                    onValueChange = { max = it },
                    label = { Text("Max Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}