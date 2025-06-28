package com.example.caloriesdiary.core.database.di

import com.example.caloriesdiary.core.database.CaloriesDiaryDatabase
import com.example.caloriesdiary.core.database.dao.FoodEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesFoodEntryDao(
        database: CaloriesDiaryDatabase,
    ): FoodEntryDao = database.foodEntryDao()
}
