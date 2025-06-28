package com.example.caloriesdiary.feature.newmeal.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.caloriesdiary.core.designsystem.icon.CaloriesDiaryIcons
import com.example.caloriesdiary.core.designsystem.theme.CaloriesDiaryTheme
import com.example.caloriesdiary.core.ui.collectInLaunchedEffectWithLifecycle
import com.example.caloriesdiary.feature.newmeal.R
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.Close
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.SaveEntry
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateCalories
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateCarbs
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateFats
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateFoodName
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateProteins
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealLabel.NavigateBack
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealLabel.ShowError
import kotlinx.coroutines.launch

@Composable
internal fun NewMealRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewMealViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var snackbarHostState = remember { SnackbarHostState() }

    viewModel.labels.collectInLaunchedEffectWithLifecycle { event ->
        launch {
            when (event) {
                is NavigateBack -> {
                    onNavigateBack()
                }

                is ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onIntent(NewMealIntent.Init)
    }

    NewMealScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewMealScreen(
    state: NewMealState,
    onIntent: (NewMealIntent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.feature_newmeal_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onIntent(Close) },
                    ) {
                        Icon(
                            imageVector = CaloriesDiaryIcons.Close,
                            contentDescription = stringResource(R.string.feature_newmeal_close),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!state.isLoading) {
                        onIntent(SaveEntry)
                    }
                },
                modifier = Modifier.alpha(if (state.isLoading) 0.5f else 1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                interactionSource = remember { MutableInteractionSource() }.apply {
                    // Block interactions when loading
                    if (state.isLoading) {
                        // This will prevent click ripple effect when disabled
                    }
                },
            ) {
                Icon(
                    imageVector = CaloriesDiaryIcons.Check,
                    contentDescription = stringResource(R.string.feature_newmeal_save),
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Name field
            NewMealInputField(
                label = stringResource(R.string.feature_newmeal_name_label),
                value = state.foodName.value,
                onValueChange = { onIntent(UpdateFoodName(it)) },
                hint = stringResource(R.string.feature_newmeal_name_hint),
                error = state.foodName.error,
                keyboardType = KeyboardType.Text,
                trailingIcon = {
                    Icon(
                        imageVector = CaloriesDiaryIcons.Person, // Using person icon as placeholder for food icon
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp),
                    )
                },
            )

            // Carbs field
            NewMealInputField(
                label = stringResource(R.string.feature_newmeal_carbs_label),
                value = state.carbs.value,
                onValueChange = { onIntent(UpdateCarbs(it)) },
                hint = stringResource(R.string.feature_newmeal_carbs_hint),
                error = state.carbs.error,
                keyboardType = KeyboardType.Number,
                trailingIcon = {
                    StarIcon()
                },
            )

            // Proteins field
            NewMealInputField(
                label = stringResource(R.string.feature_newmeal_proteins_label),
                value = state.proteins.value,
                onValueChange = { onIntent(UpdateProteins(it)) },
                hint = stringResource(R.string.feature_newmeal_proteins_hint),
                error = state.proteins.error,
                keyboardType = KeyboardType.Number,
                trailingIcon = {
                    StarIcon()
                },
            )

            // Fats field
            NewMealInputField(
                label = stringResource(R.string.feature_newmeal_fats_label),
                value = state.fats.value,
                onValueChange = { onIntent(UpdateFats(it)) },
                hint = stringResource(R.string.feature_newmeal_fats_hint),
                error = state.fats.error,
                keyboardType = KeyboardType.Number,
                trailingIcon = {
                    StarIcon()
                },
            )

            // Calories field
            NewMealInputField(
                label = stringResource(R.string.feature_newmeal_calories_label),
                value = state.calories.value,
                onValueChange = { onIntent(UpdateCalories(it)) },
                hint = stringResource(R.string.feature_newmeal_calories_hint),
                error = state.calories.error,
                keyboardType = KeyboardType.Number,
                trailingIcon = {
                    StarIcon()
                },
            )

            // Extra spacing for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StarIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = Color(0xFF404040),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = CaloriesDiaryIcons.Star,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun NewMealInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    error: String?,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    fontSize = 16.sp,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = trailingIcon,
            singleLine = true,
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                    )
                    return@OutlinedTextField
                }
                Text(
                    text = hint,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            },
        )
    }
}

@Preview
@Composable
private fun NewMealScreenPreview() {
    CaloriesDiaryTheme {
        NewMealScreen(
            state = NewMealState(
                foodName = FieldUiModel("Pizza"),
                carbs = FieldUiModel("30"),
                proteins = FieldUiModel("45"),
                fats = FieldUiModel("80"),
            ),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun NewMealScreenEmptyPreview() {
    CaloriesDiaryTheme {
        NewMealScreen(
            state = NewMealState(),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun NewMealScreenErrorPreview() {
    CaloriesDiaryTheme {
        NewMealScreen(
            state = NewMealState(
                foodName = FieldUiModel("Pizza"),
                carbs = FieldUiModel("30"),
                proteins = FieldUiModel("45"),
                fats = FieldUiModel("80"),
            ),
            onIntent = {},
        )
    }
}