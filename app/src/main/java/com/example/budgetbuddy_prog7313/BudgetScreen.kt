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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment




@Composable
fun BudgetScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val expenseDao = db.expenseDao()
    val goalManager = remember { GoalManager(context) }

    var currentSpent by remember { mutableStateOf(0.0) }

    val now = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val firstOfMonth = now.withDayOfMonth(1).format(formatter)
    val today = now.format(formatter)

    val scope = rememberCoroutineScope()

    // These control when the dialogs are shown
    var showGoalTypeDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showMonthlyDialog by remember { mutableStateOf(false) }

    // This listens for expense updates and calculates total spent this month
    LaunchedEffect(Unit) {
        expenseDao.getCategoryTotalsBetweenDates(firstOfMonth, today).collectLatest { totals ->
            currentSpent = totals.sumOf { it.total }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ThisMonthGoalSection()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Current spent this month: R${"%.2f".format(currentSpent)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lists past monthly goals
            MonthGoalListSection("Previous Months", isFuture = false)

            Spacer(modifier = Modifier.height(8.dp))

            // Lists future monthly goals
            MonthGoalListSection("Future Months", isFuture = true)

            Spacer(modifier = Modifier.height(16.dp))

            // Shows all custom goals with completion buttons
            CustomGoalListSection()
        }

        // Floating action button to start adding a goal
        FloatingActionButton(
            onClick = { showGoalTypeDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Goal")
        }
    }

    // Dialog for choosing between custom or monthly goal
    if (showGoalTypeDialog) {
        AlertDialog(
            onDismissRequest = { showGoalTypeDialog = false },
            confirmButton = {}, // Not used since we use buttons inside text
            title = { Text("Choose Goal Type") },
            text = {
                Column {
                    Button(
                        onClick = {
                            showGoalTypeDialog = false
                            showCustomDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Custom Goal")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            showGoalTypeDialog = false
                            showMonthlyDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Monthly Goal")
                    }
                }
            }
        )
    }

    // Launch the custom goal dialog
    if (showCustomDialog) {
        AddCustomGoalDialog(onDismiss = { showCustomDialog = false })
    }

    // Launch the monthly goal dialog
    if (showMonthlyDialog) {
        AddMonthlyGoalDialog(onDismiss = { showMonthlyDialog = false })
    }
}


