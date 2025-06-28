# :feature:diary module

![Diary Screen](../../images/Diary.webp)

## Overview
The **feature:diary** module implements a food diary screen that displays a paginated list of food entries with infinite scrolling capabilities. It uses custom paging implementation with MVIKotlin for state management, providing smooth performance for large datasets and real-time updates.

## Key Features
- Paginated food entries list with infinite scrolling
- Custom paging implementation (alternative to Android Paging3)
- Real-time updates with reactive data flow
- Dark-themed Material 3 UI with floating action button
- Placeholder loading states for smooth UX
- Navigation to new meal entry screen

## Module Structure
```
feature/diary/
├── src/main/kotlin/com/google/samples/apps/nowinandroid/feature/diary/
│   ├── component/meal/
│   │   └── FoodEntryCard.kt           # Custom food entry UI component
│   ├── di/
│   │   └── DiaryModule.kt             # Hilt DI configuration
│   ├── navigation/
│   │   └── DiaryNavigation.kt         # Type-safe navigation setup
│   └── presentation/
│       ├── BettingHistoryPagingDataLoader.kt      # Custom paging implementation  
│       ├── BettingHistoryUiStateBuilder.kt        # UI state transformation
│       ├── DiaryScreen.kt                          # Main Compose screen
│       ├── DiaryStoreFactory.kt                    # MVIKotlin store implementation
│       └── DiaryViewModel.kt                       # ViewModel bridge
└── src/test/kotlin/
    └── DiaryStoreTest.kt              # Unit tests (recommended)
```

## Dependencies
```kotlin
// build.gradle.kts
dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.libs.paging)
    implementation(libs.kotlinx.immutable)
    
    // Auto-provided by android.feature plugin:
    // - MVIKotlin (4.2.0)
    // - Hilt DI  
    // - Jetpack Compose + Navigation
    // - Kotlin Coroutines
}
```

## Data Models

### Core Domain Model
```kotlin
// From core:model
data class FoodEntry(
    val id: Long = 0,
    val name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val timestamp: Long,
)
```

### UI Presentation Models
```kotlin
sealed interface FoodUiModel {
    val id: BettingHistoryItemUiId

    data class Data(
        override val id: BettingHistoryItemUiId,
        val name: String,
        val calories: Float,
        val protein: Float,
        val carbs: Float,
        val fats: Float,
        val timestamp: UiDateValue,
    ) : FoodUiModel

    data class Placeholder(
        override val id: BettingHistoryItemUiId,
    ) : FoodUiModel
}
```

## MVIKotlin Architecture

### Store Contract
```kotlin
interface DiaryStore : Store<Intent, State, Label> {
    
    sealed interface Intent {
        data object Init : Intent
        data object ListOffsetReached : Intent
        data object NavigateToLogEntry : Intent
    }

    data class State(
        val foodUiModelList: ImmutableList<FoodUiModel> = persistentListOf(),
        val notificationCenterOffsetToNotify: Int? = null,
        val error: String? = null
    )

    sealed interface Label {
        data object NavigateToLogEntry : Label
    }
}
```

### Store Implementation Pattern
```kotlin
class DiaryStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val bettingHistoryPagingDataLoader: BettingHistoryPagingDataLoader,
) {
    
    private sealed interface Message {
        data class Error(val message: String) : Message
        data class Paging(val result: PagingResult<FoodEntry>) : Message
    }
    
    // Executor handles coroutines and side effects
    private inner class ExecutorImpl : 
        CoroutineExecutor<Intent, Nothing, State, Message, Label>() {
        
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Init -> initPaging()
                is ListOffsetReached -> loadNextPage()
                is NavigateToLogEntry -> publish(NavigateToLogEntry)
            }
        }
    }
    
    // Reducer performs pure state transformations
    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State =
            when (msg) {
                is Paging -> handlePagingResult(msg.result)
                is Error -> copy(error = msg.message)
            }
    }
}
```

## Custom Paging Implementation

### Paging Configuration
```kotlin
val BettingHistoryPagingConfig = PagingConfig(
    initialOffset = 0,
    limit = 10,
    firstLimit = 10,
)
```

### Paging Data Loader
```kotlin
class BettingHistoryPagingDataLoader @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    private val _pagingResultFlow = MutableSharedFlow<PagingResult<FoodEntry>>()
    val pagingResultFlow: Flow<PagingResult<FoodEntry>> = _pagingResultFlow

    fun initPagingDataLoader(scope: CoroutineScope) {
        val newLoader = PagingDataLoaderIntOffset(
            context = Job(scope.coroutineContext.job),
            dataLoader = { _, limit, offset ->
                val foodList = foodRepository.getFoodEntriesPaginated(
                    offset = offset,
                    limit = limit,
                )
                Result.success(foodList)
            },
            config = BettingHistoryPagingConfig,
        )
        
        // Start listening to paging results
        scope.launch {
            newLoader.pagingActionsAndResults.pagingResults.collect { result ->
                _pagingResultFlow.emit(result)
            }
        }
    }
}
```

### Paging Integration in UI
```kotlin
@Composable
fun DiaryScreen(state: State, onIntent: (Intent) -> Unit) {
    val onItemByIndexAccess = state.notificationCenterOffsetToNotify?.let {
        rememberPagingOnItemByIndexAccessedCallback(
            offsetToNotify = it,
            onOffsetReached = { onIntent(ListOffsetReached) },
        )
    }

    LazyColumn {
        items(count = state.foodUiModelList.size) { index ->
            onItemByIndexAccess?.invoke(index)
            FoodEntryCard(foodUiModel = state.foodUiModelList[index])
        }
    }
}
```

## UI Components

### Main Screen Structure
```kotlin
@Composable
internal fun DiaryScreen(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        containerColor = Color(0xFF1A1A1A), // Dark theme
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(NavigateToLogEntry) },
                containerColor = Color(0xFF6B46C1), // Purple
                contentColor = Color.White,
                shape = CircleShape,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(NiaIcons.Add, contentDescription = "Add")
                    Text("Log entry")
                }
            }
        },
    ) { paddingValues ->
        DiaryData(state, onIntent, modifier.padding(paddingValues))
    }
}
```

### Food Entry Card Component
```kotlin
@Composable
fun FoodEntryCard(
    foodUiModel: FoodUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(150.dp)
            .border(
                width = 1.dp, 
                shape = RoundedCornerShape(8.dp), 
                color = Color(0xFF3A3A3A)
            )
            .padding(horizontal = 20.dp),
    ) {
        when (foodUiModel) {
            is FoodUiModel.Data -> {
                Text(
                    text = foodUiModel.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                )
                Text(
                    text = "${foodUiModel.calories.toInt()} kcal",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                )
                Row {
                    NutrientChip("Carbs ${foodUiModel.carbs}g")
                    NutrientChip("Protein ${foodUiModel.protein}g") 
                    NutrientChip("Fat ${foodUiModel.fats}g")
                }
                Text(
                    text = foodUiModel.timestamp.formatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
            is FoodUiModel.Placeholder -> {
                // Shimmer loading placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}
```

## Navigation Integration

### Type-Safe Navigation
```kotlin
@Serializable 
data object DiaryRoute

fun NavController.navigateToDiary(navOptions: NavOptions? = null) =
    navigate(route = DiaryRoute, navOptions)

fun NavGraphBuilder.diaryScreen(
    onNavigateToLogEntry: () -> Unit,
) {
    composable<DiaryRoute> {
        DiaryRoute(onNavigateToLogEntry = onNavigateToLogEntry)
    }
}
```

### Route Component
```kotlin
@Composable
internal fun DiaryRoute(
    onNavigateToLogEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.labels.collectInLaunchedEffectWithLifecycle { label ->
        when (label) {
            NavigateToLogEntry -> onNavigateToLogEntry()
        }
    }

    DiaryScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
```

## State Management & Data Flow

### UI State Building
```kotlin
class BettingHistoryUiStateBuilder @Inject constructor() {
    
    fun buildUiState(
        pagingResult: PagingResult<FoodEntry>,
        currentList: ImmutableList<FoodUiModel>
    ): ImmutableList<FoodUiModel> {
        return when (pagingResult) {
            is PagingResult.Data -> {
                val newItems = pagingResult.data.map { entry ->
                    FoodUiModel.Data(
                        id = BettingHistoryItemUiId(entry.id.toString()),
                        name = entry.name,
                        calories = entry.calories,
                        protein = entry.protein,
                        carbs = entry.carbs,
                        fats = entry.fats,
                        timestamp = UiDateValue(entry.timestamp),
                    )
                }
                
                if (pagingResult.append) {
                    (currentList + newItems).toPersistentList()
                } else {
                    newItems.toPersistentList()
                }
            }
            is PagingResult.Loading -> {
                val placeholders = (0 until 3).map {
                    FoodUiModel.Placeholder(
                        id = BettingHistoryItemUiId("placeholder_$it")
                    )
                }
                (currentList + placeholders).toPersistentList()
            }
            is PagingResult.Error -> currentList
        }
    }
}
```

## Hilt/DI Setup

### Module Configuration
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DiaryModule {
    // Uses dependencies from core modules:
    // - FoodRepository from core:data
    // - StoreFactory from MVIKotlin
}
```

### ViewModel Integration
```kotlin
@HiltViewModel
class DiaryViewModel @Inject constructor(
    storeFactory: DiaryStoreFactory,
) : ViewModel() {
    
    private val store = storeFactory.create()
    val state: StateFlow<State> = store.stateFlow
    val labels: Flow<Label> = store.labels
    
    fun onIntent(intent: Intent) = store.accept(intent)
    
    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
```

## Testing Strategy

### Unit Testing
```kotlin
class DiaryStoreTest {
    @Test
    fun `when paging result received, should update food list`() = runTest {
        val store = storeFactory.create()
        val pagingResult = PagingResult.Data(
            data = listOf(testFoodEntry),
            append = false
        )
        
        store.accept(Init)
        // Verify state updates correctly
    }
    
    @Test
    fun `when list offset reached, should trigger load next page`() {
        // Test pagination trigger logic
    }
}
```

### Repository Testing
```kotlin
class BettingHistoryPagingDataLoaderTest {
    @Test
    fun `should load initial data on init`() = runTest {
        val mockRepository = mockk<FoodRepository>()
        val loader = BettingHistoryPagingDataLoader(mockRepository)
        
        // Test initial load behavior
    }
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
    namespace = "com.google.samples.apps.nowinandroid.feature.diary"
}
```

### 2. Required Dependencies
- **FoodRepository**: For data access with pagination support
- **Custom Paging Library**: `libs.paging` from `:libs:paging` module
- **kotlinx.immutable**: For efficient list operations

### 3. Key Implementation Steps
1. Define MVI contracts with paging-specific intents
2. Implement custom paging data loader
3. Create UI state builders for list transformations
4. Build LazyColumn with index-based pagination triggers
5. Add placeholder loading states
6. Implement navigation to new entry screen

### 4. Paging Configuration
- **Page Size**: 10 items per page
- **Initial Load**: 10 items
- **Trigger Offset**: Load next page when near end of current list
- **Error Handling**: Graceful fallback to current state

### 5. UI Theme Configuration
- Dark background: `Color(0xFF1A1A1A)`
- Border color: `Color(0xFF3A3A3A)`
- Purple FAB: `Color(0xFF6B46C1)`
- Loading placeholders with shimmer effect

## Architecture Benefits
- **Custom Paging**: Full control over pagination logic and performance
- **Reactive Updates**: Real-time synchronization with data layer
- **Memory Efficient**: Immutable collections with structural sharing
- **Smooth UX**: Placeholder loading states prevent jarring updates
- **Testable**: Clear separation between paging logic and UI

## External Dependencies
- **libs.paging**: Custom pagination library
- **kotlinx.immutable**: Efficient immutable collections
- **core:data**: Repository interfaces and domain models
- **MVIKotlin**: State management framework

This module demonstrates a sophisticated implementation of a paginated list feature with custom paging logic, providing excellent performance and user experience while maintaining clean architecture principles and comprehensive testing capabilities.