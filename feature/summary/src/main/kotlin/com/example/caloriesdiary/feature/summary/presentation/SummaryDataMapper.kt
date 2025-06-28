package com.example.caloriesdiary.feature.summary.presentation

import com.example.caloriesdiary.core.data.repository.food.DailyStatsModel
import com.example.caloriesdiary.feature.summary.domain.usecase.TargetStatsModel
import javax.inject.Inject

class SummaryDataMapper @Inject constructor() {

    fun mapToSummaryData(
        targetStatsModel: TargetStatsModel,
        dailyStatsModel: DailyStatsModel,
    ): SummaryData {
        return SummaryData(
            targetStats = TargetStats(
                targetCalories = targetStatsModel.targetCalories,
                targetCarbs = targetStatsModel.targetCarbs,
                targetProtein = targetStatsModel.targetProtein,
                targetFat = targetStatsModel.targetFat,
            ),
            dailyStats = DailyStats(
                totalCalories = dailyStatsModel.totalCalories,
                carbs = dailyStatsModel.carbs,
                protein = dailyStatsModel.protein,
                fat = dailyStatsModel.fat,
            ),
        )
    }
}