package com.example.caloriesdiary.feature.summary.presentation

import com.example.caloriesdiary.core.data.repository.food.DailyStatsModel
import com.example.caloriesdiary.feature.summary.domain.usecase.TargetStatsModel
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(TestParameterInjector::class)
class SummaryDataMapperTest {

    private lateinit var mapper: SummaryDataMapper

    @Before
    fun setup() {
        mapper = SummaryDataMapper()
    }

    enum class MappingTestCase(
        val targetStats: TargetStatsModel,
        val dailyStats: DailyStatsModel,
    ) {
        ZERO_VALUES(
            targetStats = TargetStatsModel(targetCalories = 0, targetProtein = 0f, targetCarbs = 0f, targetFat = 0f),
            dailyStats = DailyStatsModel(totalCalories = 0, protein = 0f, carbs = 0f, fat = 0f),
        ),
        NEGATIVE_VALUES(
            targetStats = TargetStatsModel(
                targetCalories = -100,
                targetProtein = -50f,
                targetCarbs = -75f,
                targetFat = -25f,
            ),
            dailyStats = DailyStatsModel(totalCalories = -200, protein = -60f, carbs = -80f, fat = -30f),
        ),
        LARGE_VALUES(
            targetStats = TargetStatsModel(
                targetCalories = Int.MAX_VALUE,
                targetProtein = Float.MAX_VALUE,
                targetCarbs = Float.MAX_VALUE,
                targetFat = Float.MAX_VALUE,
            ),
            dailyStats = DailyStatsModel(
                totalCalories = Int.MAX_VALUE,
                protein = Float.MAX_VALUE,
                carbs = Float.MAX_VALUE,
                fat = Float.MAX_VALUE,
            ),
        ),
        TARGET_GREATER_THAN_DAILY(
            targetStats = TargetStatsModel(
                targetCalories = 2000,
                targetProtein = 150f,
                targetCarbs = 200f,
                targetFat = 70f,
            ),
            dailyStats = DailyStatsModel(totalCalories = 1000, protein = 75f, carbs = 100f, fat = 35f),
        ),
        TARGET_LESS_THAN_DAILY(
            targetStats = TargetStatsModel(
                targetCalories = 1000,
                targetProtein = 75f,
                targetCarbs = 100f,
                targetFat = 35f,
            ),
            dailyStats = DailyStatsModel(totalCalories = 2000, protein = 150f, carbs = 200f, fat = 70f),
        ),
        TARGET_EQUALS_DAILY(
            targetStats = TargetStatsModel(
                targetCalories = 1500,
                targetProtein = 100f,
                targetCarbs = 150f,
                targetFat = 50f,
            ),
            dailyStats = DailyStatsModel(totalCalories = 1500, protein = 100f, carbs = 150f, fat = 50f),
        ),
    }

    @Test
    fun `when mapping with given values then all fields are preserved correctly`(
        @TestParameter testCase: MappingTestCase,
    ) {
        // When
        val result = mapper.mapToSummaryData(testCase.targetStats, testCase.dailyStats)

        // Then
        assertEquals(testCase.targetStats.targetCalories, result.targetStats.targetCalories)
        assertEquals(testCase.targetStats.targetProtein, result.targetStats.targetProtein)
        assertEquals(testCase.targetStats.targetCarbs, result.targetStats.targetCarbs)
        assertEquals(testCase.targetStats.targetFat, result.targetStats.targetFat)

        assertEquals(testCase.dailyStats.totalCalories, result.dailyStats.totalCalories)
        assertEquals(testCase.dailyStats.protein, result.dailyStats.protein)
        assertEquals(testCase.dailyStats.carbs, result.dailyStats.carbs)
        assertEquals(testCase.dailyStats.fat, result.dailyStats.fat)
    }
}