package com.example.caloriesdiary.feature.newmeal.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealRoute
import kotlinx.serialization.Serializable

@Serializable object NewMealRoute

fun NavController.navigateToNewMeal(navOptions: NavOptions? = null) =
    navigate(route = NewMealRoute, navOptions)

fun NavGraphBuilder.newMealScreen(
    onNavigateBack: () -> Unit,
) {
    composable<NewMealRoute> {
        NewMealRoute(
            onNavigateBack = onNavigateBack,
        )
    }
}