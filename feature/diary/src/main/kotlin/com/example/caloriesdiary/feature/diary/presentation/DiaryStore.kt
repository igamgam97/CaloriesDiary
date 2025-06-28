package com.example.caloriesdiary.feature.diary.presentation

import com.arkivanov.mvikotlin.core.store.Store
import com.example.caloriesdiary.feature.diary.component.meal.FoodUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface DiaryStore : Store<DiaryStore.Intent, DiaryStore.State, DiaryStore.Label> {
    /**
     * Diary Intent definitions for MVIKotlin Store.
     */
    sealed interface Intent {
        data object Init : Intent
        data object ListOffsetReached : Intent
        data object NavigateToLogEntry : Intent
    }

    /**
     * Diary State for MVIKotlin Store.
     */
    data class State(
        val foodUiModelList: ImmutableList<FoodUiModel> = persistentListOf(),
        val notificationCenterOffsetToNotify: Int? = null,
        val error: String? = null,
    )

    /**
     * Diary Label for one-time events from Store.
     */
    sealed interface Label {
        data object NavigateToLogEntry : Label
    }
}