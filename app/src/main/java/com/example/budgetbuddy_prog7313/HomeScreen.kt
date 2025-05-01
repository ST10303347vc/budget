package com.example.budgetbuddy_prog7313
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview



@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Welcome to Budget Buddy!")
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}