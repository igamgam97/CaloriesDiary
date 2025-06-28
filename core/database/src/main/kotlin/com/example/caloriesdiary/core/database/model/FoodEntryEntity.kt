package com.example.caloriesdiary.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Defines a food entry for tracking calorie and macronutrient intake.
 */
@Entity(
    tableName = "food_entries",
)
data class FoodEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    @ColumnInfo("created_at")
    val createdAt: Long,
)