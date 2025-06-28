package com.example.caloriesdiary.feature.parameters.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.caloriesdiary.core.designsystem.component.CaloriesBottomNavigationBar
import com.example.caloriesdiary.core.designsystem.icon.CaloriesDiaryIcons
import com.example.caloriesdiary.core.designsystem.theme.CaloriesDiaryTheme
import com.example.caloriesdiary.core.root.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.core.root.bottombar.screens
import com.example.caloriesdiary.feature.parameters.R
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent.UpdateAge
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent.UpdateHeight
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent.UpdateWeight
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.State

@Composable
internal fun ParametersRoute(
    navigateToTopRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParametersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(Intent.Init)
    }

    ParametersScreen(
        state = state,
        navigateToTopRoute = navigateToTopRoute,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ParametersScreen(
    state: State,
    navigateToTopRoute: (String) -> Unit,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.feature_parameters_title)) })
        },
        bottomBar = {
            CaloriesBottomNavigationBar(
                navigateTo = navigateToTopRoute,
                screens = screens,
                currentRoute = BottomDiaryBottomRoute.Parameters.route,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {

            ParameterCard(
                label = stringResource(R.string.feature_parameters_height),
                value = state.height,
                onValueChange = { onIntent(UpdateHeight(it)) },
                hint = stringResource(R.string.feature_parameters_height_hint),
                icon = CaloriesDiaryIcons.Person,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParameterCard(
                label = stringResource(R.string.feature_parameters_weight),
                value = state.weight,
                onValueChange = { onIntent(UpdateWeight(it)) },
                hint = stringResource(R.string.feature_parameters_weight_hint),
                icon = CaloriesDiaryIcons.Person,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParameterCard(
                label = stringResource(R.string.feature_parameters_age),
                onValueChange = { onIntent(UpdateAge(it)) },
                hint = stringResource(R.string.feature_parameters_age_hint),
                icon = CaloriesDiaryIcons.Person,
                value = state.age,
            )
        }
    }
}

@Composable
private fun ParameterCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = TextFieldValue(
                        text = value,
                        selection = TextRange(value.length), // курсор в конец
                    ),
                    onValueChange = { onValueChange(it.text) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineMedium,
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    },
                    supportingText = {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ParametersScreenPreview() {
    CaloriesDiaryTheme {
        ParametersScreen(
            state = State(
                height = "180",
                weight = "72",
                age = "20",
            ),
            onIntent = {},
            navigateToTopRoute = {},
        )
    }
}