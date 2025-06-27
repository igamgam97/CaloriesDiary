package com.example.caloriesdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caloriesdiary.bottombar.BottomDiaryBottomRoute
import com.example.caloriesdiary.bottombar.screens
import com.example.caloriesdiary.core.designsystem.component.CaloriesBottomNavigationBar
import com.example.caloriesdiary.core.designsystem.theme.CaloriesDiaryTheme

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
        composable(
            route = BottomDiaryBottomRoute.Summary.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            SummaryScreen(
                navigateToTopRoute = navController::navigateToTopRoute,
            )
        }
        composable(
            route = BottomDiaryBottomRoute.Diary.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            DiaryScreen(
                navigateToTopRoute = navController::navigateToTopRoute,
            )
        }
        composable(
            route = BottomDiaryBottomRoute.Parameters.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            ParametersScreen(
                navigateToTopRoute = navController::navigateToTopRoute,
            )
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    navigateToTopRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Diary") })
        },
        bottomBar = {
            CaloriesBottomNavigationBar(
                navigateTo = navigateToTopRoute,
                screens = screens,
                currentRoute = BottomDiaryBottomRoute.Diary.route,
            )
        },
    ) { innerPadding ->
        Text(
            text = "Diary Screen",
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametersScreen(
    navigateToTopRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Parameters") })
        },
        bottomBar = {
            CaloriesBottomNavigationBar(
                navigateTo = navigateToTopRoute,
                screens = screens,
                currentRoute = BottomDiaryBottomRoute.Parameters.route,
            )
        },
    ) { innerPadding ->
        Text(
            text = "Parameters Screen",
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