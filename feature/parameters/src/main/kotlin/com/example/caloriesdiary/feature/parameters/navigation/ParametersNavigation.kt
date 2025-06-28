package com.example.caloriesdiary.feature.parameters.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.caloriesdiary.core.root.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.feature.parameters.presentation.ParametersRoute

val parametersRoute = BottomDiaryBottomRoute.Parameters.route

fun NavController.navigateToParameters(navOptions: NavOptions? = null) =
    navigate(parametersRoute, navOptions)

fun NavGraphBuilder.parametersScreen(
    navigateToTopRoute: (String) -> Unit,
) {
    composable(
        route = parametersRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        ParametersRoute(
            navigateToTopRoute = navigateToTopRoute,
        )
    }
}