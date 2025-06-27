package com.lib.paging

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Реализация [PagingDataLoader] для постраничной загрузки данных с целочисленным смещением.
 *
 * @see PagingDataLoader
 */
class PagingDataLoaderIntOffset<Value : Any>(
    context: CoroutineContext = EmptyCoroutineContext,
    config: PagingConfig<Int>,
    dataLoader: DataLoader<Int, Value>,
) : PagingDataLoader<Int, Value>(
    context = context,
    config = config,
    dataLoader = dataLoader,
) {
    override fun hasMore(
        loadDirection: DataLoadDirection,
        limit: Int,
        offset: Int,
        data: Collection<Value>,
    ): Boolean {
        return data.size >= limit
    }

    override fun newOffset(currentOffset: Int, data: Collection<Value>) = currentOffset + data.size
}
