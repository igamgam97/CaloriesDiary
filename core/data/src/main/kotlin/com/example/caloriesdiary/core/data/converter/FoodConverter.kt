package com.example.caloriesdiary.core.data.converter

import com.example.caloriesdiary.core.database.model.FoodEntryEntity
import com.example.caloriesdiary.core.model.data.Food

fun FoodEntryEntity.asExternalModel() = Food(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fats = fat,
    timestamp = createdAt,
)

fun Food.asEntity() = FoodEntryEntity(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fats,
    createdAt = timestamp,
)