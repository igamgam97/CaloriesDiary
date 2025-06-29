package com.example.caloriesdiary.feature.summary.presentation

import com.example.caloriesdiary.core.data.repository.food.DailyStatsModel
import com.example.caloriesdiary.feature.summary.domain.usecase.TargetStatsModel

object SummaryTestData {

    fun createDefaultDailyStats() = DailyStatsModel(
        totalCalories = 1800,
        protein = 120f,
        carbs = 180f,
        fat = 60f,
    )

    fun createDefaultTargetStats() = TargetStatsModel(
        targetCalories = 2500,
        targetProtein = 160f,
        targetCarbs = 250f,
        targetFat = 80f,
    )

    fun createExceededDailyStats() = DailyStatsModel(
        totalCalories = 3000,
        protein = 200f,
        carbs = 300f,
        fat = 100f,
    )

    fun createModerateDailyStats() = DailyStatsModel(
        totalCalories = 1500,
        protein = 100f,
        carbs = 150f,
        fat = 50f,
    )

    fun createModerateTargetStats() = TargetStatsModel(
        targetCalories = 2000,
        targetProtein = 150f,
        targetCarbs = 200f,
        targetFat = 70f,
    )

    fun createZeroTargetStats() = TargetStatsModel(
        targetCalories = 0,
        targetProtein = 0f,
        targetCarbs = 0f,
        targetFat = 0f,
    )

    object ErrorMessages {
        const val DAILY_STATS_ERROR = "Failed to load daily stats"
        const val TARGET_STATS_ERROR = "Failed to evaluate target stats"
    }

    object Progress {
        const val CALORIES_75_PERCENT = 0.75f
        const val PROTEIN_67_PERCENT = 0.67f
        const val CARBS_75_PERCENT = 0.75f
        const val FAT_71_PERCENT = 0.71f
        const val MAX_PROGRESS = 1.0f
        const val ZERO_PROGRESS = 0.0f
    }
}