package com.example.caloriesdiary.feature.diary.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.caloriesdiary.core.model.data.Food
import com.example.caloriesdiary.core.ui.date.asUiDate
import com.example.caloriesdiary.feature.diary.component.meal.FoodHistoryItemUiId
import com.example.caloriesdiary.feature.diary.component.meal.FoodUiModel
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent.Init
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent.ListOffsetReached
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent.NavigateToLogEntry
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Label
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.State
import com.example.caloriesdiary.feature.diary.presentation.DiaryStoreFactory.Message.Error
import com.example.caloriesdiary.feature.diary.presentation.DiaryStoreFactory.Message.Paging
import com.example.caloriesdiary.feature.diary.presentation.DiaryUiStateBuilder.addFoodList
import com.example.caloriesdiary.feature.diary.presentation.DiaryUiStateBuilder.updateOffset
import com.lib.paging.PagingResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class DiaryStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val foodHistoryPagingDataLoader: FoodHistoryPagingDataLoader,
) {

    private val isInitialized = AtomicBoolean(false)

    fun create(): DiaryStore =
        object :
            DiaryStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "DiaryStore",
                initialState = State(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Message {

        data class Error(val message: String) : Message
        data class Paging(
            val result: PagingResult<Food>,
        ) : Message

        data object Default : Message
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<DiaryStore.Intent, Nothing, DiaryStore.State, Message, DiaryStore.Label>() {

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Init -> init()
                is ListOffsetReached -> offsetReached()
                is NavigateToLogEntry -> publish(Label.NavigateToLogEntry)
            }
        }

        private fun init() {
            foodHistoryPagingDataLoader.initPagingDataLoader(
                scope,
            )
            dispatch(Message.Default)
            if (isInitialized.getAndSet(true)) {
                return
            }
            scope.launch {
                foodHistoryPagingDataLoader.pagingResultFlow.collect {
                    dispatch(
                        Message.Paging(it),
                    )
                }
            }
        }

        private fun offsetReached() {
            scope.launch {
                foodHistoryPagingDataLoader.loadNextPage()
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State =
            when (msg) {
                is Error -> copy(
                    error = msg.message,
                )

                is Paging -> {
                    onPagingResult(msg.result)
                }

                is Message.Default -> {
                    copy(
                        foodUiModelList = persistentListOf(),
                        notificationCenterOffsetToNotify = null,
                        error = null,
                    )
                }
            }

        private fun State.onPagingResult(result: PagingResult<Food>): State {
            return when (result) {
                is PagingResult.Data -> {
                    val foodHistoryList =
                        result.data.toList()
                            .map {
                                FoodUiModel.Data(
                                    id = FoodHistoryItemUiId(it.id),
                                    name = it.name,
                                    calories = it.calories,
                                    protein = it.protein,
                                    carbs = it.carbs,
                                    fats = it.fats,
                                    timestamp = it.timestamp.asUiDate(),
                                )
                            }

                    addFoodList(
                        foodHistoryList = foodHistoryList,
                    )
                        // .hidePlaceholders()
                        .updateOffset(hasMore = result.hasMore)
                }

                is PagingResult.Error -> {
                    // hidePlaceholders()
                    updateOffset(hasMore = false)
                }

                is PagingResult.Progress -> {
                    // small database, no need to show progress
                    this
                }
            }
        }
    }
}