# Nutrition Tracking App - Architecture & Code Guidelines

## Overview

This nutrition tracking application, adapted for calorie and nutrition monitoring. Built entirely with Kotlin and Jetpack Compose, it follows Clean Architecture principles with **MVIKotlin** state management for complex UI interactions.

**📖 For detailed MVIKotlin implementation patterns, see [MVIKotlin Architecture Guide](docs/MVIKotlin_Architecture_Guide.md)**

## Architecture

### Core Principles

- **Offline-First Architecture**: Local Room database serves as the single source of truth
- **Unidirectional Data Flow**: State flows down, events flow up
- **Reactive Programming**: Kotlin Flows throughout the application
- **MVIKotlin**: MVI approach with MVIKotlin framework for predictable state management
- **Clean Architecture**: Clear separation between data, domain, and presentation layers
- **Modular Design**: Feature-based modules with clear boundaries

### Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  (Compose UI, ViewModels, UI State)                        │
├─────────────────────────────────────────────────────────────┤
│                     Domain Layer                             │
│  (Use Cases, Business Logic)                               │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                              │
│  (Repositories, Data Sources, Database)                     │
└─────────────────────────────────────────────────────────────┘
```

### Module Structure

#### App Module
- Entry point with `MainActivity` and `Application`
- Navigation orchestration via `NiaNavHost`
- Dependency injection setup with Hilt

#### Feature Modules (`feature/`)
Each feature implements MVIKotlin pattern and is self-contained with:
- **Screen Composables**: Jetpack Compose UI implementation
- **ViewModels**: Bridge between MVIKotlin stores and Compose
- **MVIKotlin Stores**: State management with Intent/State/Label pattern
- **Use Cases**: Business logic and data coordination (optional)
- **Navigation**: Type-safe navigation with Kotlin serialization

**Standard Feature Structure:**
```
feature:*/
├── di/
│   └── *Module.kt              # Hilt dependency injection
├── domain/usecase/             # Business logic (optional)
├── navigation/
│   └── *Navigation.kt          # Type-safe navigation setup
└── presentation/
    ├── *Models.kt              # MVI contracts (Intent, State, Label)
    ├── *Screen.kt              # Compose UI screens
    ├── *StoreFactory.kt        # MVIKotlin store implementation
    ├── *ViewModel.kt           # Bridge to Compose
    └── components/             # Feature-specific UI components (optional)
```

**Current Features:**
- `summary` - Daily nutrition summary with progress tracking
- `diary` - Food diary with pagination and custom entries
- `newmeal` - Form for adding new food entries with validation
- `parameters` - User body parameters (height, weight, age)

#### Core Modules (`core/`)
- `common` - Shared utilities, coroutine dispatchers, and Result wrapper
- `data` - Repository implementations with offline-first strategy  
- `database` - Room database with FoodEntry entities and DAOs
- `datastore` - Proto DataStore for user preferences and profile
- `designsystem` - Material 3 components and dark/light theming
- `domain` - Use cases for business logic (nutrition calculations)
- `model` - Shared data models (FoodEntry, UserData, DailyStats)
- `network` - Network layer (legacy from original NiA sample)
- `ui` - Reusable UI components and utilities

#### Libraries (`libs/`)
- `paging` - Custom pagination implementation for food diary

## Code Guidelines

### Kotlin Conventions

#### Naming
- **Classes**: PascalCase (e.g., `NewsRepository`)
- **Functions**: camelCase starting with verb (e.g., `getTopics()`)
- **Properties**: camelCase (e.g., `isLoading`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DEFAULT_TIMEOUT`)
- **Packages**: lowercase (e.g., `com.example.caloriesdiary`)

#### Functions
- Keep functions small and focused (< 20 lines)
- Use explicit return types
- Prefer expression bodies for simple functions
- Use default parameters instead of overloading

```kotlin
// Good
fun formatDate(
    date: Instant,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = // implementation

// Avoid
fun formatDate(date: Instant): String = formatDate(date, TimeZone.currentSystemDefault())
```

#### Data Classes
- Use data classes for holding data
- Prefer immutability with `val` properties
- Use sealed classes for representing state

```kotlin
data class NewsResourceUiState(
    val newsResource: NewsResource,
    val isBookmarked: Boolean,
    val isLoading: Boolean = false
)
```

### Android Specific Guidelines
```

#### Compose UI
- Use `remember` and `rememberSaveable` appropriately
- Prefer stateless composables
- Hoist state to the appropriate level
- Use Material 3 components from design system

```kotlin
@Composable
fun NewsResourceCard(
    newsResource: NewsResource,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stateless implementation
}
```

#### Repository Pattern
```kotlin
interface NewsRepository {
    fun getNewsResources(
        query: NewsResourceQuery = NewsResourceQuery()
    ): Flow<List<NewsResource>>
}

class OfflineFirstNewsRepository @Inject constructor(
    private val newsResourceDao: NewsResourceDao,
    private val topicDao: TopicDao,
    private val network: NiaNetworkDataSource,
    private val notifier: Notifier,
    private val syncManager: SyncManager
) : NewsRepository {
    // Offline-first implementation
}
```

### Testing Guidelines

#### Unit Tests
- Follow AAA pattern (Arrange, Act, Assert)
- Use descriptive test names
- Test public APIs, not implementation details

```kotlin
@Test
fun `uiState emits Success when topics are loaded`() = runTest {
    // Arrange
    val topics = listOf(testTopic1, testTopic2)
    topicsRepository.sendTopics(topics)
    
    // Act
    val viewModel = InterestsViewModel(topicsRepository)
    
    // Assert
    viewModel.uiState.test {
        assertEquals(
            InterestsUiState.Success(topics),
            awaitItem()
        )
    }
}
```

#### UI Tests
- Use screenshot tests for UI verification
- Test user flows, not implementation
- Use test tags for element identification

### Dependency Injection

Use Hilt for dependency injection:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindsNewsRepository(
        newsRepository: OfflineFirstNewsRepository
    ): NewsRepository
}
```

### Async Programming

- Use Kotlin Coroutines and Flows
- Follow structured concurrency principles
- Handle cancellation properly

```kotlin
viewModelScope.launch {
    userDataRepository.toggleBookmark(newsResourceId, bookmarked)
}
```

### Performance Guidelines

- Use Baseline Profiles for startup optimization
- Implement proper list performance with `LazyColumn`
- Avoid unnecessary recompositions
- Use `remember` for expensive computations

## Best Practices

### Modularization
- Keep modules focused and cohesive
- Minimize inter-module dependencies
- Use api/implementation configurations appropriately
- Follow dependency inversion principle

### Error Handling
- Use `Result` type for operations that can fail
- Handle exceptions at appropriate levels
- Provide meaningful error messages to users

### State Management
- Single source of truth for UI state
- Immutable state objects
- Clear state transitions
- Proper lifecycle handling

### Code Quality
- Consistent code formatting (use Spotless)
- Meaningful variable and function names
- Comprehensive documentation for public APIs
- Regular code reviews

## Architecture Decision Records

### Offline-First Approach
- **Decision**: Use local database as single source of truth
- **Rationale**: Better user experience with instant loading and offline support
- **Trade-offs**: More complex synchronization logic

### Modular Architecture
- **Decision**: Feature-based modularization
- **Rationale**: Improved build times, code ownership, and parallel development
- **Trade-offs**: Initial setup complexity

### Jetpack Compose
- **Decision**: Use Compose for all UI
- **Rationale**: Modern declarative UI, better state management
- **Trade-offs**: Learning curve for teams familiar with View system

### Proto DataStore
- **Decision**: Use Proto DataStore for preferences
- **Rationale**: Type safety, async API, data migration support
- **Trade-offs**: More setup than SharedPreferences

### MVIKotlin for State Management
- **Decision**: Use MVIKotlin framework for complex UI state management
- **Rationale**: Predictable state transitions, excellent testability, unidirectional data flow
- **Trade-offs**: Learning curve and additional dependency compared to traditional ViewModel approach

## Feature Module Creation Guide

### Quick Reference for Creating New Features

Each feature module should follow this structure based on our analyzed patterns:

#### 1. Module Setup
```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.caloriesdiary.android.feature)
    alias(libs.plugins.caloriesdiary.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.feature.yourfeature"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    // Additional dependencies as needed
}
```

#### 2. MVI Contract Definition
```kotlin
// YourFeatureModels.kt
sealed interface YourFeatureIntent {
    data object Init : YourFeatureIntent
    // Add your specific intents
}

data class YourFeatureState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // Add your state properties
)

sealed interface YourFeatureLabel {
    // Add one-time events
}
```

#### 3. Navigation Integration
```kotlin
// YourFeatureNavigation.kt
@Serializable
data object YourFeatureRoute

fun NavController.navigateToYourFeature() = navigate(YourFeatureRoute)

fun NavGraphBuilder.yourFeatureScreen() {
    composable<YourFeatureRoute> {
        YourFeatureRoute()
    }
}
```

This architecture and these guidelines ensure a scalable, maintainable, and testable Android application that follows modern best practices and demonstrates advanced Android development patterns with MVIKotlin state management.