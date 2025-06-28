package com.example.caloriesdiary.core.data.repository.food

import com.example.caloriesdiary.core.data.converter.asEntity
import com.example.caloriesdiary.core.data.converter.asExternalModel
import com.example.caloriesdiary.core.database.dao.FoodEntryDao
import com.example.caloriesdiary.core.database.model.FoodEntryEntity
import com.example.caloriesdiary.core.model.data.Food
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class OfflineFirstFoodRepository @Inject constructor(
    private val foodDao: FoodEntryDao,
) : FoodRepository {

    override fun getAllFoodEntries(): Flow<List<Food>> =
        foodDao.getAllFoodEntries()
            .map { entities -> entities.map(FoodEntryEntity::asExternalModel) }

    override suspend fun getFoodEntriesPaginated(
        offset: Int,
        limit: Int,
    ): List<Food> {
        return foodDao.getFoodEntriesPaginated(limit, offset)
            .map(FoodEntryEntity::asExternalModel)
    }

    override fun getFood(entryId: Long): Flow<Food?> =
        foodDao.getFoodEntry(entryId)
            .map { entity -> entity?.asExternalModel() }

    override suspend fun insertFood(entry: Food): Long {
        return foodDao.insertFoodEntry(entry.asEntity())
    }

    override suspend fun updateFood(entry: Food) {
        foodDao.updateFoodEntry(entry.asEntity())
    }

    override suspend fun deleteFood(entryId: Long) {
        foodDao.deleteFoodEntryById(entryId)
    }

    override suspend fun deleteFoodEntries(entryIds: List<Long>) {
        foodDao.deleteFoodEntriesByIds(entryIds)
    }

    override fun getDailyStatsForTimeRange(
        startTime: Long,
        endTime: Long,
    ): Flow<DailyStatsModel> =
        foodDao.getDailyStatsAggregate(startTime, endTime)
            .map { aggregate ->
                aggregate?.let {
                    DailyStatsModel(
                        totalCalories = it.totalCalories,
                        protein = it.totalProtein,
                        carbs = it.totalCarbs,
                        fat = it.totalFat,
                    )
                } ?: DailyStatsModel() // Return empty stats if no data
            }
}