package com.example.budgetbuddy_prog7313.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT category, SUM(amount) as total FROM ExpenseEntity GROUP BY category")
    fun getAllCategoryTotals(): Flow<List<CategoryTotal>>

    @Query("""
    SELECT category, SUM(amount) as total 
    FROM ExpenseEntity 
    WHERE date BETWEEN :fromDate AND :toDate 
    GROUP BY category
""")
    fun getCategoryTotalsBetweenDates(fromDate: String, toDate: String): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM ExpenseEntity ORDER BY date DESC")
    fun getAll(): Flow<List<ExpenseEntity>>
}
