# :feature:parameters module

## Overview
The **feature:parameters** module provides a user body parameters input screen where users can enter and manage their height, weight, and age information.
This data is used for calculating personalized nutrition targets and BMR (Basal Metabolic Rate). 
The module implements MVIKotlin for state management and Proto DataStore for efficient data persistence.

## Key Features
- Body parameter input form (height, weight, age)
- Real-time data synchronization with Proto DataStore
- Material 3 design with custom input fields
- Numeric input validation and filtering
- Reactive state management with MVIKotlin
- Type-safe navigation integration
- Instant data persistence on input changes

## Module Structure
```
feature/parameters/
├── src/main/kotlin/com/google/samples/apps/nowinandroid/feature/parameters/
│   ├── navigation/
│   │   └── ParametersNavigation.kt      # Type-safe navigation setup
│   └── presentation/
│       ├── ParametersModels.kt          # MVI contracts and state models
│       ├── ParametersScreen.kt          # Main Compose UI screen
│       ├── ParametersStoreFactory.kt    # MVIKotlin store implementation
│       └── ParametersViewModel.kt       # ViewModel bridge
└── src/main/res/values/
    └── strings.xml                      # String resources
```

## Dependencies
```kotlin
// build.gradle.kts
dependencies {
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    
    // Auto-provided by android.feature plugin:
    // - MVIKotlin (4.2.0)
    // - Hilt DI
    // - Jetpack Compose + Navigation
    // - Kotlin Coroutines
    // - Kotlinx Serialization
}
```

## Data Models & Proto Schema

### Domain Model
```kotlin
// From core:model
data class UserData(
    val height: Int = 0,
    val weight: Int = 0,
    val age: Int = 0,
)
```

### Proto DataStore Schema
```protobuf
// user_preferences.proto
syntax = "proto3";

package com.google.samples.apps.nowinandroid.data;

message UserPreferences {
    int32 height = 1;
    int32 weight = 2;
    int32 age = 3;
}
```

### Repository Interface
```kotlin
interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun setHeight(height: Int)
    suspend fun setWeight(weight: Int)
    suspend fun setAge(age: Int)
}
```

## MVIKotlin State Management

### MVI Contracts
```kotlin
// Store contract interface
interface ParametersStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data object Init : Intent
        data class UpdateHeight(val height: String) : Intent
        data class UpdateWeight(val weight: String) : Intent
        data class UpdateAge(val age: String) : Intent
    }

    data class State(
        val height: String = "",
        val weight: String = "",
        val age: String = "",
    )

    sealed interface Label
}
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
    namespace = "com.google.samples.apps.nowinandroid.feature.parameters"
}
```

### 2. Required Dependencies
- **UserDataRepository**: For data persistence via Proto DataStore
- **core:designsystem**: UI components and theming
- **MVIKotlin**: State management framework

### 3. Key Implementation Steps
1. Define MVI contracts (Intent, State, Label)
2. Implement store with reactive data synchronization
3. Create Material 3 UI with custom parameter cards
4. Add input validation and filtering
5. Integrate with navigation system
6. Add comprehensive testing

### 4. Data Flow Features
- **Instant Persistence**: Changes saved immediately to DataStore
- **Input Filtering**: Only numeric input allowed
- **Reactive Updates**: UI responds to repository changes
- **Zero Handling**: Empty fields treated as zero values

### 5. UI/UX Features
- **Material 3 Design**: Cards with elevation and proper spacing
- **Visual Feedback**: Icons and units for each parameter
- **Accessibility**: Proper content descriptions and labels
- **Keyboard Optimization**: Numeric keypad for input

## Architecture Benefits
- **Reactive Data Flow**: Real-time synchronization between UI and persistence
- **Type Safety**: Proto DataStore provides compile-time schema validation
- **Performance**: Efficient persistence with Protocol Buffers
- **Maintainability**: Clear separation of concerns with clean architecture
- **Testability**: Isolated components enable comprehensive testing

## External Dependencies
- **core:data**: Repository interfaces and Proto DataStore
- **core:designsystem**: UI components and theming
- **Proto DataStore**: Efficient data persistence
- **MVIKotlin**: State management framework

This module demonstrates modern Android development practices with efficient data persistence, reactive programming, and excellent user experience through real-time updates and intuitive UI design. The implementation serves as a foundation for other settings and preferences features in the application.