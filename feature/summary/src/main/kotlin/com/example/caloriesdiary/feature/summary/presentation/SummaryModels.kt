package com.example.caloriesdiary.feature.summary.presentation

data class SummaryData(
    val dailyStats: DailyStats,
    val targetStats: TargetStats,
) {
    val caloriesProgress: Float
        get() = if (targetStats.targetCalories > 0) {
            (dailyStats.totalCalories.toFloat() / targetStats.targetCalories).coerceAtMost(1f)
        } else {
            0f
        }

    val proteinProgress: Float
        get() = if (targetStats.targetProtein > 0) {
            (dailyStats.protein / targetStats.targetProtein).coerceAtMost(1f)
        } else {
            0f
        }

    val carbsProgress: Float
        get() = if (targetStats.targetCarbs > 0) {
            (dailyStats.carbs / targetStats.targetCarbs).coerceAtMost(1f)
        } else {
            0f
        }

    val fatProgress: Float
        get() = if (targetStats.targetFat > 0) {
            (dailyStats.fat / targetStats.targetFat).coerceAtMost(1f)
        } else {
            0f
        }
}

/**
 * Data model representing daily nutrition statistics.
 */
data class DailyStats(
    val totalCalories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
)

/**
 * Data model representing daily nutrition targets.
 */
data class TargetStats(
    val targetCalories: Int = 0,
    val targetProtein: Float = 0f,
    val targetCarbs: Float = 0f,
    val targetFat: Float = 0f,
)