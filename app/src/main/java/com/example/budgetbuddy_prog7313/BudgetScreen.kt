package com.example.budgetbuddy_prog7313

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.budgetbuddy_prog7313.GoalManager
import com.example.budgetbuddy_prog7313.data.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable





@Composable
fun BudgetScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val expenseDao = db.expenseDao()
    val goalManager = remember { GoalManager(context) }

    var minGoal by remember { mutableStateOf(goalManager.getMinGoal().toString()) }
    var maxGoal by remember { mutableStateOf(goalManager.getMaxGoal().toString()) }
    var currentSpent by remember { mutableStateOf(0.0) }

    val now = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val firstOfMonth = now.withDayOfMonth(1).format(formatter)
    val today = now.format(formatter)

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        expenseDao.getCategoryTotalsBetweenDates(firstOfMonth, today).collectLatest { totals ->
            currentSpent = totals.sumOf { it.total }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        OutlinedTextField(
            value = minGoal,
            onValueChange = { minGoal = it },
            label = { Text("Min Monthly Goal") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = maxGoal,
            onValueChange = { maxGoal = it },
            label = { Text("Max Monthly Goal") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                goalManager.setMinGoal(minGoal.toFloatOrNull() ?: 0f)
                goalManager.setMaxGoal(maxGoal.toFloatOrNull() ?: 0f)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Goals")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Current spent this month: R${"%.2f".format(currentSpent)}", style = MaterialTheme.typography.titleMedium)
    }
}
