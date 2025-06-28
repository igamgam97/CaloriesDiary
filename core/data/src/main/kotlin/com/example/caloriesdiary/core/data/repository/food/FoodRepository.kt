package com.example.caloriesdiary.core.data.repository.food

import com.example.caloriesdiary.core.model.data.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    /**
     * Gets all food entries as a stream.
     */
    fun getAllFoodEntries(): Flow<List<Food>>

    /**
     * Gets paginated food entries.
     */
    suspend fun getFoodEntriesPaginated(
        offset: Int,
        limit: Int,
    ): List<Food>

    /**
     * Gets a specific food entry by id.
     */
    fun getFood(entryId: Long): Flow<Food?>

    /**
     * Inserts a new food entry.
     */
    suspend fun insertFood(entry: Food): Long

    /**
     * Updates an existing food entry.
     */
    suspend fun updateFood(entry: Food)

    /**
     * Deletes a food entry by id.
     */
    suspend fun deleteFood(entryId: Long)

    /**
     * Deletes multiple food entries by ids.
     */
    suspend fun deleteFoodEntries(entryIds: List<Long>)

    /**
     * Gets aggregated daily nutrition statistics for a time range.
     */
    fun getDailyStatsForTimeRange(
        startTime: Long,
        endTime: Long,
    ): Flow<DailyStatsModel>
}

data class DailyStatsModel(
    val totalCalories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
)