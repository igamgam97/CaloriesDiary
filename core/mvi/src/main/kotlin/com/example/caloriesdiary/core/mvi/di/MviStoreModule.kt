package com.example.caloriesdiary.core.mvi.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MviStoreModule {

    @Provides
    @Singleton
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
}