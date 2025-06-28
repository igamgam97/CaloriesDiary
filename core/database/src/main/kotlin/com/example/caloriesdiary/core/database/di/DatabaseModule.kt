package com.example.caloriesdiary.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.caloriesdiary.core.database.CaloriesDiaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): CaloriesDiaryDatabase = Room.databaseBuilder(
        context,
        CaloriesDiaryDatabase::class.java,
        "nia-database",
    ).build()
}
