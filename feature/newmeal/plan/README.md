# :feature:newmeal module

![New Entry Screen](../../images/New_entry.webp)

## Overview
The **feature:newmeal** module implements a comprehensive food entry form with real-time validation, dark-themed Material 3 UI, and MVIKotlin state management.
It handles user input for creating new food entries with nutritional information and provides smooth navigation flow.

## Key Features
- Multi-field nutrition form (name, calories, carbs, protein, fat)
- Real-time validation with field-level error display
- Dark-themed Material 3 UI with purple accent color
- MVIKotlin for predictable state management
- Type-safe navigation with Kotlinx Serialization
- Loading states and error handling
- Floating action button for save functionality

## Module Structure
```
feature/newmeal/
├── src/main/kotlin/com/google/samples/apps/nowinandroid/feature/newmeal/
│   ├── di/
│   │   └── NewMealModule.kt          # Hilt DI configuration
│   ├── navigation/
│   │   └── NewMealNavigation.kt      # Type-safe navigation setup
│   └── presentation/
│       ├── NewMealModels.kt          # MVI contracts and data models
│       ├── NewMealScreen.kt          # Main Compose UI screen
│       ├── NewMealStore.kt           # MVIKotlin store interface
│       ├── NewMealStoreFactory.kt    # Store implementation
│       └── NewMealViewModel.kt       # ViewModel bridge
└── src/main/res/values/
    └── strings.xml                   # String resources
```

## Dependencies
```kotlin
// build.gradle.kts
dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.feature.summary)
    
    // Auto-provided by android.feature plugin:
    // - MVIKotlin (4.2.0)
    // - Hilt DI
    // - Jetpack Compose + Navigation
    // - Kotlin Coroutines
    // - Kotlinx Serialization
}
```

## Data Models & Form Validation

### Form State Models
```kotlin
// MVI Intent - User actions
sealed interface NewMealIntent {
    data class UpdateFoodName(val name: String) : NewMealIntent
    data class UpdateCarbs(val carbs: String) : NewMealIntent
    data class UpdateProteins(val proteins: String) : NewMealIntent
    data class UpdateFats(val fats: String) : NewMealIntent
    data class UpdateCalories(val calories: String) : NewMealIntent
    data object SaveEntry : NewMealIntent
    data object Close : NewMealIntent
}

// MVI State - Form state representation
data class NewMealState(
    val foodName: FieldUiModel = FieldUiModel.Empty,
    val carbs: FieldUiModel = FieldUiModel.Empty,
    val proteins: FieldUiModel = FieldUiModel.Empty,
    val fats: FieldUiModel = FieldUiModel.Empty,
    val calories: FieldUiModel = FieldUiModel.Empty,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Field UI model with validation support
data class FieldUiModel(
    val value: String,
    val error: String? = null,
) {
    companion object {
        val Empty = FieldUiModel(value = "")
    }
}

// MVI Label - One-time events
sealed interface NewMealLabel {
    data object NavigateBack : NewMealLabel
}
```

## MVIKotlin Store Implementation

### Store Contract
```kotlin
interface NewMealStore : Store<NewMealIntent, NewMealState, NewMealLabel>
```

## Creation Guide

### 1. Module Setup
```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.nowinandroid.android.feature)
    alias(libs.plugins.nowinandroid.android.library.compose)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.feature.newmeal"
}
```

### 2. Required Dependencies
- **FoodRepository**: For persisting food entries
- **core:data**: Domain models and data interfaces
- **MVIKotlin**: State management framework

### 3. Key Implementation Steps
1. Define MVI contracts (Intent, State, Label)
2. Implement form validation logic
3. Create MVIKotlin store with business logic
4. Build Material 3 UI with custom input fields
5. Add real-time validation feedback
6. Implement navigation integration
7. Add comprehensive testing

### 4. Form Features
- **Real-time validation**: Field-level error display
- **Keyboard optimization**: Appropriate input types for each field
- **Loading states**: Visual feedback during save operations
- **Error handling**: Both field-level and global error display

### 5. UI/UX Considerations
- **Accessibility**: Proper content descriptions and semantic structure
- **Dark theme**: Consistent with app design
- **Visual feedback**: Clear indication of validation state
- **Smooth animations**: Loading indicators and state transitions

## Architecture Benefits
- **Predictable State**: MVIKotlin ensures consistent state management
- **Real-time Validation**: Immediate feedback improves user experience
- **Type Safety**: Kotlin serialization prevents navigation errors
- **Testability**: Clear separation enables comprehensive testing
- **Maintainability**: Clean architecture facilitates future changes

## External Dependencies
- **core:data**: Repository interfaces and domain models
- **MVIKotlin**: State management framework
- **Jetpack Compose**: Modern UI framework
- **Hilt**: Dependency injection

This module demonstrates a complete implementation of a form-based feature with modern Android development practices, providing excellent user experience through real-time validation, smooth animations, and predictable state management.