package com.example.caloriesdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.caloriesdiary.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.bottombar.screens
import com.example.caloriesdiary.core.designsystem.component.CaloriesBottomNavigationBar
import com.example.caloriesdiary.core.designsystem.theme.CaloriesDiaryTheme
import com.example.caloriesdiary.feature.diary.navigation.diaryScreen
import com.example.caloriesdiary.feature.newmeal.navigation.navigateToNewMeal
import com.example.caloriesdiary.feature.newmeal.navigation.newMealScreen
import com.example.caloriesdiary.feature.parameters.navigation.parametersScreen
import com.example.caloriesdiary.feature.summary.navigation.summaryScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaloriesDiaryTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = BottomDiaryBottomRoute.Diary.route,
        modifier = modifier.fillMaxSize(),
    ) {
        parametersScreen(
            navigateToTopRoute = navController::navigateToTopRoute,
        )
        newMealScreen(
            onNavigateBack = navController::popBackStack,
        )
        diaryScreen(
            navigateToTopRoute = navController::navigateToTopRoute,
            onNavigateToMealScreen = navController::navigateToNewMeal,
        )
        summaryScreen(
            navigateToTopRoute = navController::navigateToTopRoute,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navigateToTopRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Summary") })
        },
        bottomBar = {
            CaloriesBottomNavigationBar(
                navigateTo = navigateToTopRoute,
                screens = screens,
                currentRoute = BottomDiaryBottomRoute.Summary.route,
            )
        },
    ) { innerPadding ->
        Text(
            text = "Summary Screen",
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CaloriesDiaryTheme {
        MainScreen()
    }
}

fun NavController.navigateToTopRoute(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}