package com.example.caloriesdiary.feature.summary.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.caloriesdiary.core.root.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.feature.summary.presentation.SummaryRoute

val SummaryRoute = BottomDiaryBottomRoute.Summary.route

fun NavController.navigateToSummary(navOptions: NavOptions? = null) {
    this.navigate(SummaryRoute, navOptions)
}

fun NavGraphBuilder.summaryScreen(
    navigateToTopRoute: (String) -> Unit,
) {
    composable(
        route = SummaryRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        SummaryRoute(
            navigateToTopRoute = navigateToTopRoute,
        )
    }
}