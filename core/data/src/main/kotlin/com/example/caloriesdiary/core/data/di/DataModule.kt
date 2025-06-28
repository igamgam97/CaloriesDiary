package com.example.caloriesdiary.core.data.di

import com.example.caloriesdiary.core.data.repository.food.FoodRepository
import com.example.caloriesdiary.core.data.repository.food.OfflineFirstFoodRepository
import com.example.caloriesdiary.core.data.repository.user.OfflineFirstUserDataRepository
import com.example.caloriesdiary.core.data.repository.user.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsFoodRepository(
        foodRepository: OfflineFirstFoodRepository,
    ): FoodRepository
}
