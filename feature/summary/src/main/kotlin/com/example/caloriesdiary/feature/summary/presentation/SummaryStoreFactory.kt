package com.example.caloriesdiary.feature.summary.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.caloriesdiary.feature.summary.domain.usecase.EvaluateTargetStatsUseCase
import com.example.caloriesdiary.feature.summary.domain.usecase.GetDailyStatsForTodayUseCase
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Intent
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Intent.Init
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Label
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.State
import com.example.caloriesdiary.feature.summary.presentation.SummaryStoreFactory.Action.Loading
import com.example.caloriesdiary.feature.summary.presentation.SummaryStoreFactory.Action.LoadingFailed
import com.example.caloriesdiary.feature.summary.presentation.SummaryStoreFactory.Action.SummaryDataLoaded
import com.example.caloriesdiary.feature.summary.presentation.SummaryStoreFactory.Message.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class SummaryStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val evaluateTargetStatsUseCase: EvaluateTargetStatsUseCase,
    private val getDailyStatsForTodayUseCase: GetDailyStatsForTodayUseCase,
    private val summaryDataMapper: SummaryDataMapper,
) {

    fun create(): SummaryStore =
        object :
            SummaryStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "SummaryStore",
                initialState = State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Action {
        data class SummaryDataLoaded(val summaryData: SummaryData) : Action
        data object Loading : Action
        data class LoadingFailed(val error: Throwable) : Action
    }

    private sealed interface Message {
        data class SummaryDataLoaded(val summaryData: SummaryData) : Message
        data object Loading : Message
        data class Error(val message: String) : Message
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                loadSummaryData()
                    .onStart { dispatch(Loading) }
                    .catch { dispatch(LoadingFailed(it)) }
                    .collect { dispatch(SummaryDataLoaded(it)) }
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action) {
            when (action) {
                is SummaryDataLoaded -> dispatch(Message.SummaryDataLoaded(action.summaryData))
                is Loading -> dispatch(Message.Loading)
                is LoadingFailed -> dispatch(
                    Error(
                        action.error.message ?: "Unknown error",
                    ),
                )
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Init -> Unit
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State =
            when (msg) {
                is Message.SummaryDataLoaded -> copy(
                    summaryData = msg.summaryData,
                    isLoading = false,
                )

                is Message.Loading -> copy(isLoading = true, error = null)
                is Error -> copy(isLoading = false, error = msg.message)
            }
    }

    private fun loadSummaryData(): Flow<SummaryData> {
        return getDailyStatsForTodayUseCase()
            .combine(evaluateTargetStatsUseCase()) { dailyStats, target ->
                summaryDataMapper.mapToSummaryData(
                    targetStatsModel = target,
                    dailyStatsModel = dailyStats,
                )
            }
    }
}