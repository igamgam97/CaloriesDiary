package com.example.caloriesdiary.feature.diary.presentation

import com.example.caloriesdiary.core.data.repository.food.FoodRepository
import com.example.caloriesdiary.core.model.data.Food
import com.lib.paging.PagingAction
import com.lib.paging.PagingConfig
import com.lib.paging.PagingDataLoaderIntOffset
import com.lib.paging.PagingResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject

class FoodHistoryPagingDataLoader @Inject constructor(
    private val foodRepository: FoodRepository,
) {

    private var pagingDataLoaderWithProcessingJob: Pair<Job, PagingDataLoaderIntOffset<Food>>? =
        null

    private val _pagingResultFlow = MutableSharedFlow<PagingResult<Food>>()
    val pagingResultFlow: Flow<PagingResult<Food>> = _pagingResultFlow

    fun initPagingDataLoader(
        scope: CoroutineScope,
    ) {
        return synchronized(this) {
            pagingDataLoaderWithProcessingJob?.first?.cancel()
            pagingDataLoaderWithProcessingJob?.second?.cancel()

            val newLoader = PagingDataLoaderIntOffset(
                context = Job(scope.coroutineContext.job),
                dataLoader = { _, limit, offset ->
                    val foodList = foodRepository.getFoodEntriesPaginated(
                        offset = offset,
                        limit = limit,
                    )
                    Result.success(foodList)
                },
                config = FoodHistoryPagingConfig,
            )

            val processingJob = newLoader
                .pagingResultFlow
                .onEach {
                    _pagingResultFlow.emit(it)
                }
                .launchIn(scope)

            pagingDataLoaderWithProcessingJob = processingJob to newLoader

            scope.launch { newLoader.performAction(PagingAction.LoadNextPage) }
        }
    }

    suspend fun loadNextPage() {
        pagingDataLoaderWithProcessingJob?.second?.performAction(PagingAction.LoadNextPage)
    }
}

val FoodHistoryPagingConfig = PagingConfig(
    initialOffset = 0,
    limit = 10,
    firstLimit = 10,
)

/**
 * Метод для вычисления следующего смещения для загрузки данных.
 *
 * @param resultBettingHistory список ставок, которые уже загружены
 * @param hasMore флаг, указывающий на наличие следующих данных для загрузки
 */
fun foodHistoryNextOffsetToLoad(
    resultBettingHistory: List<Any>,
    hasMore: Boolean,
): Int? {
    return if (hasMore) {
        (resultBettingHistory.size - FoodHistoryPagingConfig.limit)
    } else {
        null
    }
}
