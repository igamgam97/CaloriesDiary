package com.example.caloriesdiary.feature.summary.domain.usecase

import app.cash.turbine.test
import com.example.caloriesdiary.core.data.repository.user.UserDataRepository
import com.example.caloriesdiary.core.model.data.UserData
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(TestParameterInjector::class)
class EvaluateTargetStatsUseCaseTest {

    private lateinit var userDataRepository: UserDataRepository
    private lateinit var useCase: EvaluateTargetStatsUseCase

    @Before
    fun setup() {
        userDataRepository = mockk()
        useCase = EvaluateTargetStatsUseCase(userDataRepository)
    }

    @Test
    fun `when user data is provided then BMR is calculated using Mifflin-St Jeor equation for male`() = runTest {
        // Given
        val userData = UserData(
            weight = 80, // kg
            height = 180, // cm
            age = 30,
        )
        every { userDataRepository.userData } returns flowOf(userData)

        // When & Then
        useCase().test {
            val result = awaitItem()

            // BMR for male = (10 × weight) + (6.25 × height) - (5 × age) + 5
            // BMR = (10 × 80) + (6.25 × 180) - (5 × 30) + 5
            // BMR = 800 + 1125 - 150 + 5 = 1780
            // Daily calories = BMR × 1.55 (moderate activity) = 1780 × 1.55 = 2759
            assertEquals(2759, result.targetCalories)

            awaitComplete()
        }
    }

    @Test
    fun `when calories are calculated then macronutrients follow 25-45-30 distribution`() = runTest {
        // Given
        val userData = UserData(
            weight = 70,
            height = 175,
            age = 35,
        )
        every { userDataRepository.userData } returns flowOf(userData)

        // When & Then
        useCase().test {
            val result = awaitItem()

            // Calculate expected values
            val bmr = (10 * 70) + (6.25 * 175) - (5 * 35) + 5
            val calories = (bmr * 1.55).toInt()

            // Protein: 25% of calories / 4 cal/g
            val expectedProtein = (calories * 0.25 / 4).toFloat()
            // Carbs: 45% of calories / 4 cal/g
            val expectedCarbs = (calories * 0.45 / 4).toFloat()
            // Fat: 30% of calories / 9 cal/g
            val expectedFat = (calories * 0.30 / 9).toFloat()

            assertEquals(calories, result.targetCalories)
            assertEquals(expectedProtein, result.targetProtein, 0.01f)
            assertEquals(expectedCarbs, result.targetCarbs, 0.01f)
            assertEquals(expectedFat, result.targetFat, 0.01f)

            awaitComplete()
        }
    }

    enum class EdgeCaseScenario(
        val userData: UserData,
        val expectedCalories: Int,
    ) {
        ZERO_WEIGHT(
            userData = UserData(weight = 0, height = 170, age = 30),
            expectedCalories = 1422, // BMR = 0 + 1062.5 - 150 + 5 = 917.5, Calories = 917.5 * 1.55 = 1422
        ),
        ZERO_HEIGHT(
            userData = UserData(weight = 70, height = 0, age = 30),
            expectedCalories = 860, // BMR = 700 + 0 - 150 + 5 = 555, Calories = 555 * 1.55 = 860.25 ≈ 860
        ),
        ZERO_AGE(
            userData = UserData(weight = 70, height = 170, age = 0),
            expectedCalories = 2739, // BMR = 700 + 1062.5 - 0 + 5 = 1767.5, Calories = 1767.5 * 1.55 = 2739.625 ≈ 2739
        ),
        NEGATIVE_VALUES(
            userData = UserData(weight = -70, height = -170, age = -30),
            expectedCalories = -2491, // BMR = -700 - 1062.5 + 150 + 5 = -1607.5, Calories = -1607.5 * 1.55 = -2491.625 ≈ -2491
        ),
        VERY_HIGH_VALUES(
            userData = UserData(weight = 200, height = 220, age = 80),
            expectedCalories = 4619, // BMR = 2000 + 1375 - 400 + 5 = 2980, Calories = 2980 * 1.55 = 4619
        ),
        TYPICAL_YOUNG_MALE(
            userData = UserData(weight = 75, height = 180, age = 25),
            expectedCalories = 2720, // BMR = 750 + 1125 - 125 + 5 = 1755, Calories = 1755 * 1.55 = 2720.25 ≈ 2720
        ),
        TYPICAL_OLDER_PERSON(
            userData = UserData(weight = 65, height = 160, age = 60),
            expectedCalories = 2100, // BMR = 650 + 1000 - 300 + 5 = 1355, Calories = 1355 * 1.55 = 2100.25 ≈ 2100
        ),
    }

    @Test
    fun `when edge case values are provided then calculations handle them correctly`(
        @TestParameter edgeCase: EdgeCaseScenario,
    ) = runTest {
        // Given
        every { userDataRepository.userData } returns flowOf(edgeCase.userData)

        // When & Then
        useCase().test {
            val result = awaitItem()
            assertEquals(edgeCase.expectedCalories, result.targetCalories)
            awaitComplete()
        }
    }

    @Test
    fun `when repository emits multiple values then all are transformed correctly`() = runTest {
        // Given
        val userData1 = UserData(weight = 70, height = 170, age = 30)
        val userData2 = UserData(weight = 65, height = 165, age = 28)
        val userData3 = UserData(weight = 80, height = 180, age = 35)

        every { userDataRepository.userData } returns flowOf(userData1, userData2, userData3)

        // When & Then
        useCase().test {
            // First emission - userData1: weight=70, height=170, age=30
            // BMR = 10*70 + 6.25*170 - 5*30 + 5 = 700 + 1062.5 - 150 + 5 = 1617.5
            // Calories = 1617.5 * 1.55 = 2507.125 ≈ 2507
            val result1 = awaitItem()
            assertEquals(2507, result1.targetCalories)

            // Second emission - userData2: weight=65, height=165, age=28
            // BMR = 10*65 + 6.25*165 - 5*28 + 5 = 650 + 1031.25 - 140 + 5 = 1546.25
            // Calories = 1546.25 * 1.55 = 2396.6875 ≈ 2396
            val result2 = awaitItem()
            assertEquals(2396, result2.targetCalories)

            // Third emission - userData3: weight=80, height=180, age=35
            // BMR = 10*80 + 6.25*180 - 5*35 + 5 = 800 + 1125 - 175 + 5 = 1755
            // Calories = 1755 * 1.55 = 2720.25 ≈ 2720
            val result3 = awaitItem()
            assertEquals(2720, result3.targetCalories)

            awaitComplete()
        }
    }

    @Test
    fun `when macronutrient calculation results in decimal then values are preserved as float`() = runTest {
        // Given - values chosen to create decimal results
        val userData = UserData(
            weight = 73,
            height = 177,
            age = 33,
        )
        every { userDataRepository.userData } returns flowOf(userData)

        // When & Then
        useCase().test {
            val result = awaitItem()

            // Verify calculations
            val calories = result.targetCalories

            // These should have decimal values
            assertEquals((calories * 0.25 / 4).toFloat(), result.targetProtein, 0.01f)
            assertEquals((calories * 0.45 / 4).toFloat(), result.targetCarbs, 0.01f)
            assertEquals((calories * 0.30 / 9).toFloat(), result.targetFat, 0.01f)

            awaitComplete()
        }
    }

    @Test
    fun `when all user data is zero then minimal BMR is calculated`() = runTest {
        // Given
        val userData = UserData(
            weight = 0,
            height = 0,
            age = 0,
        )
        every { userDataRepository.userData } returns flowOf(userData)

        // When & Then
        useCase().test {
            val result = awaitItem()

            // BMR = 10*0 + 6.25*0 - 5*0 + 5 = 5
            // Calories = 5 * 1.55 = 7.75 ≈ 7
            assertEquals(7, result.targetCalories)

            awaitComplete()
        }
    }

    @Test
    fun `when user data changes then new values are calculated correctly`() = runTest {
        // Given - simulate weight loss journey
        val startingData = UserData(weight = 90, height = 175, age = 30)
        val afterWeightLoss = UserData(weight = 80, height = 175, age = 30)

        every { userDataRepository.userData } returns flowOf(startingData, afterWeightLoss)

        // When & Then
        useCase().test {
            // Initial calculation - weight=90, height=175, age=30
            // BMR = 10*90 + 6.25*175 - 5*30 + 5 = 900 + 1093.75 - 150 + 5 = 1848.75
            // Calories = 1848.75 * 1.55 = 2865.5625 ≈ 2865
            val initialResult = awaitItem()
            assertEquals(2865, initialResult.targetCalories)

            // After weight loss - weight=80, height=175, age=30
            // BMR = 10*80 + 6.25*175 - 5*30 + 5 = 800 + 1093.75 - 150 + 5 = 1748.75
            // Calories = 1748.75 * 1.55 = 2710.5625 ≈ 2710
            val updatedResult = awaitItem()
            assertEquals(2710, updatedResult.targetCalories)

            // Verify that calories decreased with weight loss
            assert(updatedResult.targetCalories < initialResult.targetCalories)

            awaitComplete()
        }
    }

    @Test
    fun `when extreme values are used then calculation doesn't overflow`() = runTest {
        // Given
        val userData = UserData(
            weight = 1000, // Very heavy
            height = 300, // Very tall
            age = 150, // Very old
        )
        every { userDataRepository.userData } returns flowOf(userData)

        // When & Then
        useCase().test {
            val result = awaitItem()

            // BMR = 10*1000 + 6.25*300 - 5*150 + 5 = 11130
            // Calories = 11130 * 1.55 = 17_251.5 ≈ 17_251
            assertEquals(17_251, result.targetCalories)

            // Verify macros are calculated without overflow
            assert(result.targetProtein > 0)
            assert(result.targetCarbs > 0)
            assert(result.targetFat > 0)

            awaitComplete()
        }
    }
}