# :feature:summary module

![Summary Screen](../../images/summary.webp)

## Overview
The **feature:summary** module provides a comprehensive daily nutrition summary screen displaying progress towards calorie and macronutrient targets. It implements MVI pattern with MVIKotlin for reactive state management and calculates personalized nutrition goals based on user body parameters.

## Key Features
- Daily nutrition progress tracking (calories, protein, carbs, fat)
- Personalized target calculation using Mifflin-St Jeor equation
- Real-time progress indicators with circular progress bars
- Dark-themed Material 3 UI design
- Reactive state management with MVIKotlin

## Module Structure
```
feature/summary/
├── src/main/kotlin/com/google/samples/apps/nowinandroid/feature/summary/
│   ├── di/
│   │   └── SummaryModule.kt           # Hilt DI configuration
│   ├── domain/usecase/
│   │   ├── EvaluateTargetStatsUseCase.kt    # BMR calculation logic
│   │   └── GetDailyStatsForTodayUseCase.kt  # Daily stats aggregation
│   ├── navigation/
│   │   └── SummaryNavigation.kt       # Type-safe navigation setup
│   └── presentation/
│       ├── SummaryDataMapper.kt       # Data transformation layer
│       ├── SummaryModels.kt           # MVI state models
│       ├── SummaryScreen.kt           # Compose UI screens
│       ├── SummaryStore.kt            # MVIKotlin store interface
│       ├── SummaryStoreFactory.kt     # Store implementation
│       └── SummaryViewModel.kt        # ViewModel bridge
└── src/test/kotlin/
    └── SummaryStoreTest.kt            # Unit tests
```

## Dependencies
```kotlin
// build.gradle.kts
dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    
    // Auto-provided by android.feature plugin:
    // - MVIKotlin (4.2.0)
    // - Hilt DI
    // - Jetpack Compose + Navigation
    // - Kotlin Coroutines
    // - Kotlinx DateTime
}
```

## Data Models

### Core Domain Models
```kotlin
data class SummaryData(
    val dailyStats: DailyStats,
    val targetStats: TargetStats,
) {
    // Computed progress properties
    val caloriesProgress: Float
    val proteinProgress: Float
    val carbsProgress: Float
    val fatProgress: Float
}

data class DailyStats(
    val totalCalories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
)

data class TargetStats(
    val targetCalories: Int = 0,
    val targetProtein: Float = 0f,
    val targetCarbs: Float = 0f,
    val targetFat: Float = 0f,
)
```

## MVIKotlin Architecture

### Store Contract
```kotlin
interface SummaryStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data object Init : Intent
    }
    
    data class State(
        val selectedDate: LocalDate = LocalDate(2024, 1, 1),
        val summaryData: SummaryData? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    sealed interface Label {
        data class ShowError(val message: String) : Label
    }
}
```

### Store Implementation Pattern
- **Bootstrapper**: Auto-loads data on store creation
- **Executor**: Handles business logic and coroutines
- **Reducer**: Pure state transformations
- **Messages**: Internal communication between components

## Business Logic (Use Cases)

### Target Calculation (EvaluateTargetStatsUseCase)
```kotlin
// Mifflin-St Jeor Equation for BMR calculation
val bmr = 10 * weight + 6.25 * height - 5 * age + 5
val calories = (bmr * 1.55).toInt() // Moderate activity level

// Macronutrient distribution
val protein = (calories * 0.25 / 4).toFloat() // 25% protein
val carbs = (calories * 0.45 / 4).toFloat()   // 45% carbs
val fat = (calories * 0.30 / 9).toFloat()     // 30% fat
```

### Daily Stats Aggregation (GetDailyStatsForTodayUseCase)
- Uses system timezone for day boundaries
- Aggregates food entries for current day
- Returns reactive Flow for real-time updates

## Creation Guide

### 1. Module Setup
```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.nowinandroid.android.feature)
    alias(libs.plugins.nowinandroid.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.feature.summary"
}
```

### 2. Core Dependencies Required
- **UserDataRepository**: For user profile data
- **FoodRepository**: For daily nutrition data aggregation
- **MVIKotlin StoreFactory**: For state management

### 3. Key Implementation Steps
1. Define MVI contracts (Intent, State, Label)
2. Implement use cases for business logic
3. Create MVIKotlin store with executor/reducer pattern
4. Build Compose UI with progress indicators
5. Add navigation integration
6. Implement comprehensive testing

### 4. Theme Configuration
- Dark color scheme (0xFF121212 background)
- Purple accent color (0xFFB794F6)
- Material 3 components with custom colors
- Circular progress indicators for visual feedback

## Architecture Benefits
- **Reactive UI**: Real-time updates through Flow-based data layer
- **Testability**: Clean separation enables isolated unit testing
- **Maintainability**: MVIKotlin pattern provides predictable state management
- **Reusability**: Use cases can be shared across features
- **Performance**: Efficient state updates with immutable data structures

## External Dependencies
- **kotlinx.datetime**: For date/time calculations
- **core:data**: Repository interfaces and data models
- **core:designsystem**: UI components and theming
- **MVIKotlin**: State management framework

This module demonstrates a sophisticated implementation of a nutrition tracking feature with modern Android architecture patterns, comprehensive testing, and excellent user experience through reactive programming and custom UI components.