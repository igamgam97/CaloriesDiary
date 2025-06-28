package com.example.caloriesdiary.feature.diary.presentation

import com.example.caloriesdiary.feature.diary.component.meal.FoodHistoryItemUiId
import com.example.caloriesdiary.feature.diary.component.meal.FoodUiModel
import kotlinx.collections.immutable.toImmutableList

object DiaryUiStateBuilder {

    fun DiaryStore.State.addFoodList(
        foodHistoryList: List<FoodUiModel>,
    ): DiaryStore.State {
        return copy(
            foodUiModelList = (foodUiModelList + foodHistoryList)
                .distinctBy { it.id }
                .toImmutableList(),
        )
    }

    fun DiaryStore.State.hidePlaceholders(): DiaryStore.State {
        return copy(
            foodUiModelList = foodUiModelList
                .filterIsInstance<FoodUiModel.Data>()
                .toImmutableList(),
        )
    }

    fun DiaryStore.State.updateOffset(hasMore: Boolean): DiaryStore.State {
        return copy(
            notificationCenterOffsetToNotify = foodHistoryNextOffsetToLoad(
                hasMore = hasMore,
                resultBettingHistory = foodUiModelList,
            ),
        )
    }

    fun DiaryStore.State.showInitialPlaceholders(): DiaryStore.State {
        val placeholders =
            foodHistoryPlaceholderModelSequence().take(FoodHistoryPagingConfig.limit).toList()

        return copy(
            foodUiModelList = (foodUiModelList + placeholders).toImmutableList(),
        )
    }

    fun DiaryStore.State.showPlaceholder(): DiaryStore.State {
        val placeholders = foodHistoryPlaceholderModelSequence().take(1).toList()

        return copy(
            foodUiModelList = (foodUiModelList + placeholders).toImmutableList(),
        )
    }

    fun DiaryStore.State.init(): DiaryStore.State {
        return copy()
            .showInitialPlaceholders()
    }
}

fun bettingHistoryPlaceholderModel(id: Long) = FoodUiModel.Placeholder(FoodHistoryItemUiId(-id))

fun foodHistoryPlaceholderModelSequence(): Sequence<FoodUiModel.Placeholder> {
    return sequence {
        var id = 1
        while (true) {
            yield(bettingHistoryPlaceholderModel(id.toLong()))
            id++
        }
    }
}