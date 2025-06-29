package com.example.caloriesdiary.feature.summary.presentation

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.caloriesdiary.core.data.repository.food.DailyStatsModel
import com.example.caloriesdiary.feature.summary.domain.usecase.EvaluateTargetStatsUseCase
import com.example.caloriesdiary.feature.summary.domain.usecase.GetDailyStatsForTodayUseCase
import com.example.caloriesdiary.feature.summary.domain.usecase.TargetStatsModel
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Intent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SummaryStoreFactoryTest {

    private val storeFactory = DefaultStoreFactory()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
        Dispatchers.resetMain()
    }

    @Test
    fun loads_data_immediately_WHEN_created() {
        // Test the actual behavior: with Dispatchers.Unconfined,
        // bootstrapper runs immediately and data loads synchronously
        val store = createStore()

        val state = store.state
        // Since bootstrapper runs immediately with Unconfined dispatcher,
        // we expect data to be loaded right away
        assertNotNull(state.summaryData)
        assertFalse(state.isLoading) // Loading completes immediately
        assertNull(state.error)
    }

    @Test
    fun loads_summary_data_WHEN_bootstrapped() {
        val dailyStats = SummaryTestData.createDefaultDailyStats()
        val targetStats = SummaryTestData.createDefaultTargetStats()

        val store = createStore(
            dailyStatsFlow = flowOf(dailyStats),
            targetStatsFlow = flowOf(targetStats),
        )

        val state = store.state
        assertNotNull(state.summaryData)
        assertFalse(state.isLoading)
        assertNull(state.error)

        val summaryData = state.summaryData!!
        assertEquals(targetStats.targetCalories, summaryData.targetStats.targetCalories)
        assertEquals(targetStats.targetProtein, summaryData.targetStats.targetProtein)
        assertEquals(targetStats.targetCarbs, summaryData.targetStats.targetCarbs)
        assertEquals(targetStats.targetFat, summaryData.targetStats.targetFat)

        assertEquals(dailyStats.totalCalories, summaryData.dailyStats.totalCalories)
        assertEquals(dailyStats.protein, summaryData.dailyStats.protein)
        assertEquals(dailyStats.carbs, summaryData.dailyStats.carbs)
        assertEquals(dailyStats.fat, summaryData.dailyStats.fat)
    }

    @Test
    fun shows_error_WHEN_daily_stats_fails() {
        val errorMessage = SummaryTestData.ErrorMessages.DAILY_STATS_ERROR

        val store = createStore(
            dailyStatsFlow = flow { error(errorMessage) },
            targetStatsFlow = flowOf(SummaryTestData.createDefaultTargetStats()),
        )

        val state = store.state
        assertNull(state.summaryData)
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun shows_error_WHEN_target_stats_fails() {
        val errorMessage = SummaryTestData.ErrorMessages.TARGET_STATS_ERROR

        val store = createStore(
            dailyStatsFlow = flowOf(SummaryTestData.createDefaultDailyStats()),
            targetStatsFlow = flow { error(errorMessage) },
        )

        val state = store.state
        assertNull(state.summaryData)
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun calculates_progress_correctly_WHEN_data_loaded() {
        val dailyStats = SummaryTestData.createModerateDailyStats()
        val targetStats = SummaryTestData.createModerateTargetStats()

        val store = createStore(
            dailyStatsFlow = flowOf(dailyStats),
            targetStatsFlow = flowOf(targetStats),
        )

        val state = store.state
        assertNotNull(state.summaryData)

        val summaryData = state.summaryData!!
        assertEquals(SummaryTestData.Progress.CALORIES_75_PERCENT, summaryData.caloriesProgress)
        assertEquals(
            SummaryTestData.Progress.PROTEIN_67_PERCENT,
            summaryData.proteinProgress,
            0.01f,
        )
        assertEquals(SummaryTestData.Progress.CARBS_75_PERCENT, summaryData.carbsProgress)
        assertEquals(SummaryTestData.Progress.FAT_71_PERCENT, summaryData.fatProgress, 0.01f)
    }

    @Test
    fun caps_progress_at_one_WHEN_values_exceed_targets() {
        val dailyStats = SummaryTestData.createExceededDailyStats()
        val targetStats = SummaryTestData.createModerateTargetStats()

        val store = createStore(
            dailyStatsFlow = flowOf(dailyStats),
            targetStatsFlow = flowOf(targetStats),
        )

        val state = store.state
        val summaryData = state.summaryData!!

        assertEquals(SummaryTestData.Progress.MAX_PROGRESS, summaryData.caloriesProgress)
        assertEquals(SummaryTestData.Progress.MAX_PROGRESS, summaryData.proteinProgress)
        assertEquals(SummaryTestData.Progress.MAX_PROGRESS, summaryData.carbsProgress)
        assertEquals(SummaryTestData.Progress.MAX_PROGRESS, summaryData.fatProgress)
    }

    @Test
    fun shows_zero_progress_WHEN_targets_are_zero() {
        val dailyStats = SummaryTestData.createModerateDailyStats()
        val targetStats = SummaryTestData.createZeroTargetStats()

        val store = createStore(
            dailyStatsFlow = flowOf(dailyStats),
            targetStatsFlow = flowOf(targetStats),
        )

        val state = store.state
        val summaryData = state.summaryData!!

        assertEquals(SummaryTestData.Progress.ZERO_PROGRESS, summaryData.caloriesProgress)
        assertEquals(SummaryTestData.Progress.ZERO_PROGRESS, summaryData.proteinProgress)
        assertEquals(SummaryTestData.Progress.ZERO_PROGRESS, summaryData.carbsProgress)
        assertEquals(SummaryTestData.Progress.ZERO_PROGRESS, summaryData.fatProgress)
    }

    @Test
    fun preserves_state_WHEN_init_intent_sent() {
        val dailyStats = SummaryTestData.createDefaultDailyStats()
        val targetStats = SummaryTestData.createDefaultTargetStats()

        val store = createStore(
            dailyStatsFlow = flowOf(dailyStats),
            targetStatsFlow = flowOf(targetStats),
        )

        val initialState = store.state

        store.accept(Intent.Init)

        val finalState = store.state
        assertEquals(initialState, finalState)
    }

    @Test
    fun creates_independent_instances_WHEN_multiple_stores_created() {
        val store1 = createStore()
        val store2 = createStore()

        assertTrue(store1 !== store2)
    }

    private fun createStore(
        dailyStatsFlow: kotlinx.coroutines.flow.Flow<DailyStatsModel> =
            flowOf(SummaryTestData.createDefaultDailyStats()),
        targetStatsFlow: kotlinx.coroutines.flow.Flow<TargetStatsModel> =
            flowOf(SummaryTestData.createDefaultTargetStats()),
    ): SummaryStore {
        val getDailyStatsForTodayUseCase = mockk<GetDailyStatsForTodayUseCase>()
        every { getDailyStatsForTodayUseCase.invoke() } returns dailyStatsFlow

        val evaluateTargetStatsUseCase = mockk<EvaluateTargetStatsUseCase>()
        every { evaluateTargetStatsUseCase.invoke() } returns targetStatsFlow

        val summaryDataMapper = SummaryDataMapper()

        val factory = SummaryStoreFactory(
            storeFactory = storeFactory,
            getDailyStatsForTodayUseCase = getDailyStatsForTodayUseCase,
            evaluateTargetStatsUseCase = evaluateTargetStatsUseCase,
            summaryDataMapper = summaryDataMapper,
        )

        return factory.create()
    }
}