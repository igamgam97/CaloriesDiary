package com.lib.paging

/**
 * Конфигурация для постраничной загрузки данных [PagingDataLoader].
 *
 * @param initialOffset начальное смещение для загрузки данных (от которого начинается отсчет)
 * @param limit максимальное количество элементов, которое может быть загружено за один запрос
 * @param firstLimit максимальное количество элементов, которое может быть загружено за первый запрос
 */
data class PagingConfig<Key : Any>(
    val initialOffset: Key,
    val limit: Int,
    val firstLimit: Int = limit,
)
