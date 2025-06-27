package com.lib.paging

/**
 * Класс, описывающий виды результатов постраничной загрузки данных в [PagingDataLoader].
 */
sealed interface PagingResult<T : Any> {
    /**
     * Состояние, описывающее, что загрузка данных в процессе.
     *
     * @property loadDirection тип загрузки данных
     */
    data class Progress<T : Any>(
        val loadDirection: DataLoadDirection,
    ) : PagingResult<T>

    /**
     * Состояние, описывающее, что загрузка данных завершена успешно.
     *
     * @property data загруженные данные
     * @property loadDirection тип загрузки данных
     * @property hasMore флаг, описывающий, есть ли еще данные для загрузки
     */
    data class Data<T : Any>(
        val data: Collection<T>,
        val loadDirection: DataLoadDirection,
        val hasMore: Boolean,
    ) : PagingResult<T>

    /**
     * Состояние, описывающее, что загрузка данных завершена с ошибкой.
     *
     * @property throwable ошибка, которая произошла при загрузке данных
     */
    data class Error<T : Any>(
        val throwable: Throwable,
    ) : PagingResult<T>
}

/**
 * Класс, описывающий виды направлений загрузки данных в [PagingDataLoader].
 */
sealed interface DataLoadDirection {
    /**
     * Направление для загрузки следующей порции данных.
     */
    data object Append : DataLoadDirection

    /**
     * Направление для перезапуска загрузки данных.
     */
    data object Restart : DataLoadDirection
}

/**
 * Класс, описывающий виды запросов на загрузку данных в [PagingDataLoader].
 */
sealed interface PagingAction {
    /**
     * Запрос на загрузку следующей порции данных.
     */
    data object LoadNextPage : PagingAction

    /**
     * Запрос на перезапуск загрузки данных.
     */
    data object Refresh : PagingAction
}