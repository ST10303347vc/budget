package com.example.budgetbuddy_prog7313

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgetbuddy_prog7313.LevelManager

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val levelManager = remember { LevelManager(context) }

    var level by remember { mutableStateOf(levelManager.getLevel()) }
    var xp by remember { mutableStateOf(levelManager.getXP()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🧑", style = MaterialTheme.typography.headlineLarge)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Level $level", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = xp,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Progress: ${(xp * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
    }
}

