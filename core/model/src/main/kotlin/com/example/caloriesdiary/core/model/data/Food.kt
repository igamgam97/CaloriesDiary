package com.example.caloriesdiary.core.model.data

data class Food(
    val id: Long = 0,
    val name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val timestamp: Long,
)