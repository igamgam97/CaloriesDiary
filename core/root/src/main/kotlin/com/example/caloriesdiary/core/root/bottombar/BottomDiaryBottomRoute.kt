package com.example.caloriesdiary.core.root.bottombar

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.caloriesdiary.core.designsystem.component.BottomRoute
import com.example.caloriesdiary.core.designsystem.icon.CaloriesDiaryIcons
import kotlinx.collections.immutable.persistentListOf

val screens = persistentListOf(
    BottomDiaryBottomRoute.Summary,
    BottomDiaryBottomRoute.Diary,
    BottomDiaryBottomRoute.Parameters,
)

sealed class BottomDiaryBottomRoute(
    override val route: String,
    override val title: String,
    override val icon: ImageVector,
) : BottomRoute {
    data object Summary : BottomDiaryBottomRoute(
        route = "summary",
        title = "Summary",
        icon = CaloriesDiaryIcons.List,
    )

    data object Diary : BottomDiaryBottomRoute(
        route = "diary",
        title = "Diary",
        icon = CaloriesDiaryIcons.Diary,
    )

    data object Parameters : BottomDiaryBottomRoute(
        route = "parameters",
        title = "Parameters",
        icon = CaloriesDiaryIcons.Parameters,
    )
}