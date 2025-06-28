package com.example.caloriesdiary.feature.summary.domain.usecase

import com.example.caloriesdiary.core.data.repository.food.DailyStatsModel
import com.example.caloriesdiary.core.data.repository.food.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetDailyStatsForTodayUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {

    operator fun invoke(): Flow<DailyStatsModel> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(timeZone)
        val startOfDay = today.atStartOfDayIn(timeZone)
        val endOfDay = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)

        val startTime = startOfDay.toEpochMilliseconds()
        val endTime = endOfDay.toEpochMilliseconds() - 1

        return foodRepository.getDailyStatsForTimeRange(startTime, endTime)
    }
}