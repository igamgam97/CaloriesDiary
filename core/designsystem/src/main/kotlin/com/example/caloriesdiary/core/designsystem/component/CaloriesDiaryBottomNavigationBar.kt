package com.example.caloriesdiary.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CaloriesBottomNavigationBar(
    navigateTo: (String) -> Unit,
    screens: ImmutableList<BottomRoute>,
    currentRoute: String,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navigateTo(screen.route)
                },
            )
        }
    }
}

interface BottomRoute {
    val route: String
    val title: String
    val icon: ImageVector
}