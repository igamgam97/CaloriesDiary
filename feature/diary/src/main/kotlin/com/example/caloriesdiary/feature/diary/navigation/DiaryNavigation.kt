package com.example.caloriesdiary.feature.diary.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.caloriesdiary.core.root.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.feature.diary.presentation.DiaryRoute

val DiaryRoute = BottomDiaryBottomRoute.Diary.route

fun NavController.navigateToDiary(navOptions: NavOptions? = null) =
    navigate(route = DiaryRoute, navOptions)

fun NavGraphBuilder.diaryScreen(
    navigateToTopRoute: (String) -> Unit,
    onNavigateToMealScreen: () -> Unit,
) {
    composable(
        route = DiaryRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        DiaryRoute(
            navigateToTopRoute = navigateToTopRoute,
            onNavigateToLogEntry = onNavigateToMealScreen,
        )
    }
}