package com.example.caloriesdiary.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.caloriesdiary.core.database.dao.FoodEntryDao
import com.example.caloriesdiary.core.database.model.FoodEntryEntity
import com.example.caloriesdiary.core.database.util.InstantConverter

@Database(
    entities = [
        FoodEntryEntity::class,
    ],
    version = 1,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class CaloriesDiaryDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao
}
