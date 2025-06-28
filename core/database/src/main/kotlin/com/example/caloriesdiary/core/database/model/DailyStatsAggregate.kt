package com.example.caloriesdiary.core.database.model

import androidx.room.ColumnInfo

/**
 * Data class for aggregated daily nutrition statistics from database.
 */
data class DailyStatsAggregate(
    @ColumnInfo("total_calories")
    val totalCalories: Int,
    @ColumnInfo("total_protein")
    val totalProtein: Float,
    @ColumnInfo("total_carbs")
    val totalCarbs: Float,
    @ColumnInfo("total_fat")
    val totalFat: Float,
)