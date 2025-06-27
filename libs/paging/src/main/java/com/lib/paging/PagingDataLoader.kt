package com.lib.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Интерфейс загрузчика данных для [PagingDataLoader].
 */
fun interface DataLoader<Key : Any, Value : Any> {
    /**
     * Метод для загрузки данных.
     *
     * @param loadDirection тип загрузки данных (может определять в каком направлении необходимо загружать данные)
     * @param limit максимальное количество элементов, которое может быть загружено за один запрос
     * @param offset смещение, от которого необходимо загружать данные
     */
    suspend fun loadData(loadDirection: DataLoadDirection, limit: Int, offset: Key): Result<Collection<Value>>
}

/**
 * Класс, реализующий постраничную загрузку данных.
 *
 * Абстрактный, относительно типа смещения [Key], что необходимо для реализации, как стариц с целочисленным смещением,
 * так и со смещением в виде даты или строк.
 *
 * Класс работает в UDF режиме:
 * - На вход получает [PagingAction] (который может быть как запросом на загрузку следующей порции данных, так и
 * запросом на обновление данных) через метод [performAction].
 * - На выходе возвращает [PagingResult] через [pagingResultFlow] с соответствующим состоянием загрузки данных
 * [pagingResultFlow].
 *
 * @param context контекст в котором будут обрабатываться запросы [performAction] на загрузку данных
 * @property config конфигурация для постраничной загрузки данных
 * @property dataLoader загрузчик данных
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class PagingDataLoader<Key : Any, Value : Any>(
    context: CoroutineContext = EmptyCoroutineContext,
    val config: PagingConfig<Key>,
    private val dataLoader: DataLoader<Key, Value>,
) {
    private val _actionFlow = MutableSharedFlow<PagingAction>(
        // Для кейсов, когда job для обработки _actionFlow запустится внутри нового scope позже, чем будет
        // отправлено первое действие, сразу после вызова конструктора.
        replay = 1,
    )

    protected val _pagingResultFlow = MutableSharedFlow<PagingResult<Value>>(
        replay = 1, // Для возможности получения последнего результата при подписке
    )

    /**
     * Flow с результатами загрузки данных.
     *
     * Содержит внутри себя кеш 1 последнего результата, который идентифицирует актуальное состояние процесса
     * загрузки данных.
     */
    val pagingResultFlow: SharedFlow<PagingResult<Value>> = _pagingResultFlow.asSharedFlow()

    private val _currentOffsetFlow = MutableStateFlow(config.initialOffset)

    /**
     * Flow с текущим смещением загрузки данных.
     */
    val currentOffsetFlow: StateFlow<Key> = _currentOffsetFlow.asStateFlow()

    protected val scope = CoroutineScope(context)

    init {
        _actionFlow
            .onEach { handleAction(it) }
            .launchIn(scope)
    }

    /**
     * Метод для выполнения запроса постраничной загрузки данных [action].
     *
     * Конкурентные запросы передаваемые в performAction будут обработаны последовательно, каждый последующий вызов
     * [performAction] будет ожидать завершения обработки предыдущего.
     *
     * @param action действие, которое необходимо выполнить.
     */
    suspend fun performAction(action: PagingAction) {
        _actionFlow.emit(action)
    }

    /**
     * Метод для завершения обработки запросов на загрузку данных.
     *
     * @param cause причина завершения обработки запросов на загрузку данных.
     */
    fun cancel(cause: CancellationException? = null) {
        scope.cancel(cause)
    }

    /**
     * Метод, определяющий наличие следующих страниц с данными для загрузки.
     *
     * @param loadDirection тип загрузки данных
     * @param limit максимальное количество элементов, которое может быть загружено за один запрос
     * @param offset смещение, от которого необходимо загружать данные
     * @param data данные, полученные в результате загрузки.
     */
    protected abstract fun hasMore(
        loadDirection: DataLoadDirection,
        limit: Int,
        offset: Key,
        data: Collection<Value>,
    ): Boolean

    /**
     * Метод, определяющий новое смещение для загрузки данных.
     *
     * @param currentOffset текущее смещение
     * @param data данные, полученные в результате предыдущей загрузки.
     */
    protected abstract fun newOffset(currentOffset: Key, data: Collection<Value>): Key

    private suspend fun handleAction(action: PagingAction) {
        when (action) {
            is PagingAction.LoadNextPage -> loadPortionData(DataLoadDirection.Append)
            is PagingAction.Refresh -> loadPortionData(DataLoadDirection.Restart)
        }
    }

    private suspend fun loadPortionData(loadDirection: DataLoadDirection) {
        if (loadDirection == DataLoadDirection.Restart) _currentOffsetFlow.value = config.initialOffset
        _pagingResultFlow.emit(PagingResult.Progress(loadDirection))

        val requestLimit = if (_currentOffsetFlow.value == config.initialOffset) {
            config.firstLimit
        } else {
            config.limit
        }
        val requestOffset = _currentOffsetFlow.value

        val loadResult = dataLoader.loadData(
            loadDirection = loadDirection,
            offset = requestOffset,
            limit = requestLimit,
        )

        loadResult.fold(
            onSuccess = { data ->
                _currentOffsetFlow.update { currentOffset -> newOffset(currentOffset, data) }
                _pagingResultFlow.emit(
                    PagingResult.Data(
                        data = data,
                        loadDirection = loadDirection,
                        hasMore = hasMore(
                            loadDirection = loadDirection,
                            offset = requestOffset,
                            limit = requestLimit,
                            data = data,
                        ),
                    ),
                )
            },
            onFailure = {
                _pagingResultFlow.emit(PagingResult.Error(it))
            },
        )
    }
}