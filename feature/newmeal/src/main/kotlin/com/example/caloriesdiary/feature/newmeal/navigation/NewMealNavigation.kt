package com.example.caloriesdiary.feature.newmeal.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealRoute

const val NewMealRoute = "new_meal_route"

fun NavController.navigateToNewMeal(navOptions: NavOptions? = null) =
    navigate(route = NewMealRoute, navOptions)

fun NavGraphBuilder.newMealScreen(
    onNavigateBack: () -> Unit,
) {
    composable(
        route = NewMealRoute,
    ) {
        NewMealRoute(
            onNavigateBack = onNavigateBack,
        )
    }
}