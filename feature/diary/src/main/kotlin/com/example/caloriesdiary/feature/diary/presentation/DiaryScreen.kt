package com.example.caloriesdiary.feature.diary.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.caloriesdiary.core.designsystem.component.CaloriesBottomNavigationBar
import com.example.caloriesdiary.core.designsystem.icon.CaloriesDiaryIcons
import com.example.caloriesdiary.core.designsystem.theme.CaloriesDiaryTheme
import com.example.caloriesdiary.core.root.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.core.root.bottombar.screens
import com.example.caloriesdiary.core.ui.collectInLaunchedEffectWithLifecycle
import com.example.caloriesdiary.core.ui.date.asUiDate
import com.example.caloriesdiary.feature.diary.R
import com.example.caloriesdiary.feature.diary.component.meal.FoodEntryCard
import com.example.caloriesdiary.feature.diary.component.meal.FoodHistoryItemUiId
import com.example.caloriesdiary.feature.diary.component.meal.FoodUiModel
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent.Init
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent.NavigateToLogEntry
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Label
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.State
import com.lib.paging.rememberPagingOnItemByIndexAccessedCallback
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDateTime

@Composable
internal fun DiaryRoute(
    navigateToTopRoute: (String) -> Unit,
    onNavigateToLogEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.labels.collectInLaunchedEffectWithLifecycle {
        when (it) {
            Label.NavigateToLogEntry -> {
                onNavigateToLogEntry()
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onIntent(Init)
    }

    DiaryScreen(
        state = state,
        navigateToTopRoute = navigateToTopRoute,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiaryScreen(
    state: State,
    navigateToTopRoute: (String) -> Unit,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.feature_diary_title)) })
        },
        bottomBar = {
            CaloriesBottomNavigationBar(
                navigateTo = navigateToTopRoute,
                screens = screens,
                currentRoute = BottomDiaryBottomRoute.Diary.route,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(NavigateToLogEntry) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = CaloriesDiaryIcons.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.feature_diary_log_entry),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        },
    ) { paddingValues ->
        DiaryData(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 26.dp, vertical = 24.dp),
            onIntent = onIntent,
        )
    }
}

@Composable
private fun DiaryData(
    state: DiaryStore.State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {

    val listState = rememberLazyListState()

    val onBettingHistoryByIndexAccess = state.notificationCenterOffsetToNotify?.let {
        rememberPagingOnItemByIndexAccessedCallback(
            offsetToNotify = it,
            onOffsetReached = { onIntent(Intent.ListOffsetReached) },
        )
    }
    Column(
        modifier = modifier,
    ) {
        if (state.foodUiModelList.isEmpty() && state.error == null) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.feature_diary_empty_state),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp), // Space for FAB
            ) {
                items(
                    count = state.foodUiModelList.size,
                    key = {
                        val entry = state.foodUiModelList[it]
                        entry.id.value
                    },
                ) { index ->
                    onBettingHistoryByIndexAccess?.invoke(index)
                    val entry = state.foodUiModelList[index]
                    FoodEntryCard(
                        foodUiModel = entry,
                    )
                }
            }
        }
    }
}

@Suppress("LongMethod")
@Preview
@Composable
private fun DiaryScreenPreview() {
    CaloriesDiaryTheme {
        DiaryScreen(
            state = State(
                foodUiModelList = persistentListOf(
                    FoodUiModel.Data(
                        id = FoodHistoryItemUiId(1),
                        name = "Pizza",
                        calories = 810f,
                        carbs = 30f,
                        protein = 45f,
                        fats = 80f,
                        timestamp = LocalDateTime(
                            year = 2025,
                            monthNumber = 12,
                            dayOfMonth = 31,
                            hour = 23,
                            minute = 59,
                            second = 59,
                        ).asUiDate(),
                    ),
                    FoodUiModel.Data(
                        id = FoodHistoryItemUiId(2),
                        name = "Apple",
                        calories = 120f,
                        carbs = 30f,
                        protein = 0f,
                        fats = 0f,
                        timestamp = LocalDateTime(
                            year = 2025,
                            monthNumber = 12,
                            dayOfMonth = 31,
                            hour = 23,
                            minute = 59,
                            second = 59,
                        ).asUiDate(),
                    ),
                    FoodUiModel.Data(
                        id = FoodHistoryItemUiId(3),
                        name = "Break",
                        calories = 240f,
                        carbs = 80f,
                        protein = 20f,
                        fats = 0f,
                        timestamp = LocalDateTime(
                            year = 2025,
                            monthNumber = 12,
                            dayOfMonth = 31,
                            hour = 23,
                            minute = 59,
                            second = 59,
                        ).asUiDate(),
                    ),
                ),
            ),
            onIntent = {},
            navigateToTopRoute = {},
        )
    }
}

@Preview
@Composable
private fun DiaryScreenEmptyPreview() {
    CaloriesDiaryTheme {
        DiaryScreen(
            state = State(),
            onIntent = {},
            navigateToTopRoute = {},
        )
    }
}