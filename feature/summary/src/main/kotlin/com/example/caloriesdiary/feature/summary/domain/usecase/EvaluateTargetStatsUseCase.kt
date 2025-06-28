@file:Suppress("MagicNumber")

package com.example.caloriesdiary.feature.summary.domain.usecase

import com.example.caloriesdiary.core.data.repository.user.UserDataRepository
import com.example.caloriesdiary.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EvaluateTargetStatsUseCase @Inject constructor(
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(): Flow<TargetStatsModel> {
        return userDataRepository.userData.map {
            evaluate(it)
        }
    }

    private suspend fun evaluate(userData: UserData): TargetStatsModel {
        val userInfo = userDataRepository.userData.first()
        val weight = userInfo.weight.toDouble() // Int -> Double для точности вычислений
        val height = userInfo.height.toDouble() // Int -> Double для точности вычислений
        val age = userInfo.age.toDouble() // Int -> Double для точности вычислений

        // Mifflin-St Jeor Equation for BMR (assuming male for simplicity)
        val bmr = 10 * weight + 6.25 * height - 5 * age + 5
        val calories = (bmr * 1.55).toInt() // Moderate activity level

        // Calculate macros based on standard ratios
        val protein = (calories * 0.25 / 4).toFloat() // 25% protein, 4 cal/g
        val carbs = (calories * 0.45 / 4).toFloat() // 45% carbs, 4 cal/g
        val fat = (calories * 0.30 / 9).toFloat() // 30% fat, 9 cal/g

        return TargetStatsModel(
            targetCalories = calories,
            targetProtein = protein,
            targetCarbs = carbs,
            targetFat = fat,
        )
    }
}

data class TargetStatsModel(
    val targetCalories: Int = 0,
    val targetProtein: Float = 0f,
    val targetCarbs: Float = 0f,
    val targetFat: Float = 0f,
)