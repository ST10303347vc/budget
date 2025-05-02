package com.example.budgetbuddy_prog7313
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Movie
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import android.app.DatePickerDialog
import com.example.budgetbuddy_prog7313.CategoryItem
import com.example.budgetbuddy_prog7313.data.CategoryTotal
import kotlinx.coroutines.launch
import com.example.budgetbuddy_prog7313.data.ExpenseEntity

import com.example.budgetbuddy_prog7313.data.AppDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Expenses", "Categories")
    var showDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        showDialog = true
                    } else if (selectedTab == 1) {
                        showCategoryDialog = true
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            when (selectedTab) {
                0 -> ExpenseListScreen()
                1 -> CategoryListScreen()
            }

            if (selectedTab == 0 && showDialog) {
                AddExpenseDialog(onDismiss = { showDialog = false })
            }

            if (selectedTab == 1 && showCategoryDialog) {
                AddCategoryDialog(onDismiss = { showCategoryDialog = false })
            }
        }
    }
}






@Composable
fun ExpenseListScreen() {
    var showCategoryDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val expenseDao = db.expenseDao()
    val expenses: List<ExpenseEntity> by expenseDao.getAll().collectAsState(initial = emptyList())

    // Trying to fix the filter
    LaunchedEffect(expenses) {
        expenses.forEach {
            println("Expense: ${it.name} | Date: ${it.date}")
        }
    }

    if (expenses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No expenses yet")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(expenses) { expense ->
                ExpenseItem(
                    name = expense.name,
                    description = expense.description,
                    category = expense.category,
                    amount = expense.amount
                )
            }
        }
    }
}




@Composable
fun ExpenseItem(name: String, description: String, category: String, amount: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
                Text(text = "Category: $category", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "R${"%.2f".format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

        }
    }
}


// Data class for expenses
data class Expense(
    val name: String,
    val category: String,
    val amount: Double,
    val icon: ImageVector
)



@Composable
fun CategoryListScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val expenseDao = db.expenseDao()

    var showDateDialog by remember { mutableStateOf(false) }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var categoryTotals by remember { mutableStateOf<List<CategoryTotal>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // Load all totals by default
    LaunchedEffect(Unit) {
        expenseDao.getAllCategoryTotals().collectLatest {
            categoryTotals = it
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Button(
            onClick = { showDateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filter by Date Range")
        }
        Button(
            onClick = {
                fromDate = ""
                toDate = ""
                scope.launch {
                    expenseDao.getAllCategoryTotals().collectLatest {
                        categoryTotals = it
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Filter", color = MaterialTheme.colorScheme.onError)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (categoryTotals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No category totals found.")
            }
        } else {
            LazyColumn {
                items(categoryTotals) { total ->
                    CategoryItem(name = total.category, total = total.total)
                }
            }
        }
    }

    if (showDateDialog) {
        DateRangeDialog(
            onConfirm = { from, to ->
                fromDate = from
                toDate = to
                scope.launch {
                    expenseDao.getCategoryTotalsBetweenDates(fromDate, toDate).collectLatest {
                        categoryTotals = it
                        showDateDialog = false
                    }
                }
            },
            onDismiss = { showDateDialog = false }
        )
    }
}




@Composable
fun CategoryItem(name: String, total: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text("R${"%.2f".format(total)}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
