package com.example.budgetbuddy_prog7313.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MonthlyGoal(
    @PrimaryKey val monthId: String, // e.g., "2025-05"
    val minAmount: Float,
    val maxAmount: Float
)
