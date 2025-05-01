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
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Expenses", "Categories")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        // Add new expense
                    } else {
                        // Add new category
                    }
                }
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> ExpenseListScreen()
                1 -> CategoryListScreen()
            }
        }
    }
}

@Composable
fun ExpenseListScreen() {
    val expenses = listOf(
        Expense("Groceries", "Food", 1250.50, Icons.Default.Fastfood),
        Expense("Uber Ride", "Transport", 180.00, Icons.Default.DirectionsCar),
        Expense("Movie Tickets", "Entertainment", 450.75, Icons.Default.Movie)
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(expenses) { expense ->
            ExpenseItem(expense)
        }
    }
}


@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Material Icon
            Icon(
                imageVector = expense.icon,
                contentDescription = expense.category,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Category: ${expense.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = "R${"%.2f".format(expense.amount)}",
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
    val categories = listOf(
        "Food" to 1250.50,
        "Transport" to 780.00,
        "Entertainment" to 450.75
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(categories) { (name, total) ->
            CategoryItem(name = name, total = total)
        }
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = String.format("R%.2f", total),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
