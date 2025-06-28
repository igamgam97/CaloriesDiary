package com.example.caloriesdiary.feature.summary.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.caloriesdiary.core.designsystem.component.CaloriesBottomNavigationBar
import com.example.caloriesdiary.core.designsystem.theme.CaloriesDiaryTheme
import com.example.caloriesdiary.core.root.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.core.root.bottombar.screens
import com.example.caloriesdiary.feature.summary.R
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Intent
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.State

@Composable
fun SummaryRoute(
    navigateToTopRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(Intent.Init)
    }

    SummaryScreen(
        state = state,
        navigateToTopRoute = navigateToTopRoute,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    state: State,
    navigateToTopRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.feature_summary_title),
                    )
                },
            )
        },
        bottomBar = {
            CaloriesBottomNavigationBar(
                navigateTo = navigateToTopRoute,
                screens = screens,
                currentRoute = BottomDiaryBottomRoute.Summary.route,
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 26.dp, vertical = 24.dp),
        ) {
            when {
                state.isLoading -> {
                    LoadingContent()
                }

                state.summaryData != null -> {
                    SummaryContent(
                        summaryData = state.summaryData,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SummaryContent(
    summaryData: SummaryData,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        item {
            NutritionProgressCard(
                title = stringResource(R.string.feature_summary_total_intake),
                current = summaryData.dailyStats.totalCalories,
                target = summaryData.targetStats.targetCalories,
                unit = stringResource(R.string.feature_summary_kcal_unit),
                progress = summaryData.caloriesProgress,
            )
        }

        item {
            NutritionProgressCard(
                title = stringResource(R.string.feature_summary_carbs),
                current = summaryData.dailyStats.carbs.toInt(),
                target = summaryData.targetStats.targetCarbs.toInt(),
                unit = stringResource(R.string.feature_summary_gram_unit),
                progress = summaryData.carbsProgress,
            )
        }

        item {
            NutritionProgressCard(
                title = stringResource(R.string.feature_summary_fat),
                current = summaryData.dailyStats.fat.toInt(),
                target = summaryData.targetStats.targetFat.toInt(),
                unit = stringResource(R.string.feature_summary_gram_unit),
                progress = summaryData.fatProgress,
            )
        }

        item {
            NutritionProgressCard(
                title = stringResource(R.string.feature_summary_protein),
                current = summaryData.dailyStats.protein.toInt(),
                target = summaryData.targetStats.targetProtein.toInt(),
                unit = stringResource(R.string.feature_summary_gram_unit),
                progress = summaryData.proteinProgress,
            )
        }
    }
}

@Composable
private fun NutritionProgressCard(
    title: String,
    current: Int,
    target: Int,
    unit: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp),
        ) {
            // Background circle
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
            )
            // Progress circle
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text content
        Column(
            modifier = Modifier,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.feature_summary_progress_format,
                    current,
                    target,
                    unit,
                ),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Preview
@Composable
fun SummaryScreenPreview() {
    CaloriesDiaryTheme {
        SummaryScreen(
            state = State(
                isLoading = false,
                summaryData = SummaryData(
                    dailyStats = DailyStats(
                        totalCalories = 2000,
                        carbs = 250f,
                        fat = 70f,
                        protein = 100f,
                    ),
                    targetStats = TargetStats(
                        targetCalories = 2200,
                        targetCarbs = 300f,
                        targetFat = 80f,
                        targetProtein = 120f,
                    ),
                ),
            ),
            navigateToTopRoute = {},
        )
    }
}