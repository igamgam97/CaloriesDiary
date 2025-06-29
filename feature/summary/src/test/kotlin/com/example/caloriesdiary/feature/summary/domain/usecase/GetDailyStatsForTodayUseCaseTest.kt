package com.example.caloriesdiary.feature.summary.domain.usecase

import app.cash.turbine.test
import com.example.caloriesdiary.core.data.repository.food.DailyStatsModel
import com.example.caloriesdiary.core.data.repository.food.FoodRepository
import com.example.caloriesdiary.feature.summary.presentation.SummaryTestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetDailyStatsForTodayUseCaseTest {

    private lateinit var foodRepository: FoodRepository
    private lateinit var useCase: GetDailyStatsForTodayUseCase
    private val testClock = Clock.System

    @Before
    fun setup() {
        foodRepository = mockk()
        useCase = GetDailyStatsForTodayUseCase(foodRepository)
    }

    @Test
    fun `when invoked then returns daily stats for today's time range`() = runTest {
        // Given
        val expectedStats = SummaryTestData.createDefaultDailyStats()
        val today = testClock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = today.atStartOfDayIn(TimeZone.currentSystemDefault())
        val endOfDay =
            today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault())

        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } returns flowOf(expectedStats)

        // When & Then
        useCase().test {
            val result = awaitItem()
            assertEquals(expectedStats, result)
            awaitComplete()

            // Verify correct time range was used
            coVerify {
                foodRepository.getDailyStatsForTimeRange(
                    startTime = match {
                        it >= startOfDay.toEpochMilliseconds() && it < startOfDay.plus(
                            1,
                            DateTimeUnit.MINUTE,
                        ).toEpochMilliseconds()
                    },
                    endTime = match { it >= endOfDay.toEpochMilliseconds() - 60_000 && it <= endOfDay.toEpochMilliseconds() },
                )
            }
        }
    }

    @Test
    fun `when repository returns multiple emissions then all are emitted`() = runTest {
        // Given
        val stats1 = DailyStatsModel(totalCalories = 1000, protein = 50f, carbs = 100f, fat = 30f)
        val stats2 = DailyStatsModel(totalCalories = 1500, protein = 75f, carbs = 150f, fat = 45f)
        val stats3 = DailyStatsModel(totalCalories = 2000, protein = 100f, carbs = 200f, fat = 60f)

        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } returns flowOf(stats1, stats2, stats3)

        // When & Then
        useCase().test {
            assertEquals(stats1, awaitItem())
            assertEquals(stats2, awaitItem())
            assertEquals(stats3, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when repository throws exception then exception is propagated`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } throws exception

        // When & Then
        try {
            useCase().test {
                // Should throw before getting here
            }
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals("Database error", e.message)
        }
    }

    @Test
    fun `when invoked at different times of day then correct date range is used`() = runTest {
        // Given
        val expectedStats = SummaryTestData.createDefaultDailyStats()
        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } returns flowOf(expectedStats)

        // When - invoke the use case
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then - verify the time range spans exactly one day
        val startTimeSlot = slot<Long>()
        val endTimeSlot = slot<Long>()
        coVerify {
            foodRepository.getDailyStatsForTimeRange(
                startTime = capture(startTimeSlot),
                endTime = capture(endTimeSlot),
            )
        }

        val duration = endTimeSlot.captured - startTimeSlot.captured
        // Should be approximately 24 hours in milliseconds (allowing for small variations)
        assert(duration in (23 * 60 * 60 * 1_000)..(25 * 60 * 60 * 1_000))
    }

    @Test
    fun `when timezone changes then correct local date is used`() = runTest {
        // Given
        val expectedStats = SummaryTestData.createDefaultDailyStats()
        val currentTimeZone = TimeZone.currentSystemDefault()
        val now = testClock.now()
        val localDate = now.toLocalDateTime(currentTimeZone).date

        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } returns flowOf(expectedStats)

        // When
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then - verify the date matches the current system timezone
        coVerify {
            foodRepository.getDailyStatsForTimeRange(
                startTime = match { startTime ->
                    val startInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(startTime)
                    val startDate = startInstant.toLocalDateTime(currentTimeZone).date
                    startDate == localDate
                },
                endTime = match { endTime ->
                    val endInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(endTime)
                    val endDate = endInstant.toLocalDateTime(currentTimeZone).date
                    endDate == localDate
                },
            )
        }
    }

    @Test
    fun `when repository returns empty flow then empty flow is emitted`() = runTest {
        // Given
        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } returns flowOf()

        // When & Then
        useCase().test {
            awaitComplete()
        }
    }

    @Test
    fun `when invoked then uses current system timezone`() = runTest {
        // Given
        val expectedStats = SummaryTestData.createDefaultDailyStats()
        val systemTimeZone = TimeZone.currentSystemDefault()

        coEvery {
            foodRepository.getDailyStatsForTimeRange(any(), any())
        } returns flowOf(expectedStats)

        // When
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then - verify that the time range respects the system timezone
        coVerify {
            foodRepository.getDailyStatsForTimeRange(
                startTime = match { startTime ->
                    // Start should be at midnight in the system timezone
                    val startInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(startTime)
                    val localDateTime = startInstant.toLocalDateTime(systemTimeZone)
                    localDateTime.hour == 0 && localDateTime.minute == 0 && localDateTime.second == 0
                },
                endTime = match { endTime ->
                    // End should be close to midnight of the next day (endTime is actually endOfDay - 1ms)
                    val endInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(endTime + 1)
                    val localDateTime = endInstant.toLocalDateTime(systemTimeZone)
                    localDateTime.hour == 0 && localDateTime.minute == 0 && localDateTime.second == 0
                },
            )
        }
    }
}