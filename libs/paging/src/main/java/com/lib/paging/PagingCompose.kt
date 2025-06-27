package com.lib.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filterNotNull

/**
 * Метод для создания колбека, связывающий событие обращения к элементу списка с колбеком,
 * который будет вызван, когда достигнут заданный порог (необходимый для загрузки следующей порции данных).
 *
 * Грубо говоря, метод сужает поток событий достижения индексов в поток уникальных индексов, являющихся порогами.
 * Например: [0, 1, 2, ..., 20, ..., 40, ..., 20] -> [20, 40].
 *
 * @param offsetToNotify порог, при достижении которого будет вызван [onOffsetReached]
 * @param onOffsetReached колбек, который будет вызван, когда достигнут порог [offsetToNotify]. При этом колбек
 * будет вызван только один раз, пока не будет обновлен [offsetToNotify].
 * @return колбек, который нужно передать в [LazyListState.onItemIndexAccessed]
 */
@Composable
fun rememberPagingOnItemByIndexAccessedCallback(
    offsetToNotify: Int,
    onOffsetReached: (offset: Int) -> Unit,
): (index: Int) -> Unit {
    var reachedOffset by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(onOffsetReached) {
        snapshotFlow { reachedOffset }
            .filterNotNull()
            .collect { onOffsetReached(it) }
    }

    return remember(offsetToNotify) {
        val onItemIndexAccessed = { index: Int ->
            if (index >= offsetToNotify) reachedOffset = offsetToNotify
        }

        onItemIndexAccessed
    }
}