package com.example.caloriesdiary.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.caloriesdiary.core.database.model.DailyStatsAggregate
import com.example.caloriesdiary.core.database.model.FoodEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [FoodEntryEntity] access.
 */
@Dao
interface FoodEntryDao {

    @Query(
        value = """
        SELECT * FROM food_entries
        WHERE id = :entryId
    """,
    )
    fun getFoodEntry(entryId: Long): Flow<FoodEntryEntity?>

    @Query(value = "SELECT * FROM food_entries ORDER BY created_at DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntryEntity>>

    @Query(
        value = """
        SELECT * FROM food_entries 
        ORDER BY created_at DESC 
        LIMIT :limit OFFSET :offset
    """,
    )
    suspend fun getFoodEntriesPaginated(
        limit: Int,
        offset: Int,
    ): List<FoodEntryEntity>

    /**
     * Inserts a food entry into the db.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(entry: FoodEntryEntity): Long

    /**
     * Updates a food entry in the db.
     */
    @Update
    suspend fun updateFoodEntry(entry: FoodEntryEntity)

    /**
     * Deletes a food entry from the db.
     */
    @Delete
    suspend fun deleteFoodEntry(entry: FoodEntryEntity)

    /**
     * Deletes a food entry by id.
     */
    @Query(
        value = """
            DELETE FROM food_entries
            WHERE id = :entryId
        """,
    )
    suspend fun deleteFoodEntryById(entryId: Long)

    /**
     * Deletes multiple food entries by ids.
     */
    @Query(
        value = """
            DELETE FROM food_entries
            WHERE id in (:entryIds)
        """,
    )
    suspend fun deleteFoodEntriesByIds(entryIds: List<Long>)

    /**
     * Gets aggregated daily nutrition statistics for a time range.
     */
    @Query(
        value = """
            SELECT 
                COALESCE(SUM(calories), 0) as total_calories,
                COALESCE(SUM(protein), 0.0) as total_protein,
                COALESCE(SUM(carbs), 0.0) as total_carbs,
                COALESCE(SUM(fat), 0.0) as total_fat
            FROM food_entries
            WHERE created_at BETWEEN :startTime AND :endTime
        """,
    )
    fun getDailyStatsAggregate(
        startTime: Long,
        endTime: Long,
    ): Flow<DailyStatsAggregate?>
}