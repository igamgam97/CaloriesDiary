# MVIKotlin Architecture Guide for Now in Android

## Overview

MVIKotlin is a Kotlin Multiplatform MVI framework that provides a robust implementation of the Model-View-Intent pattern. This guide outlines how to integrate MVIKotlin into new features while maintaining compatibility with the existing MVVM architecture.

## Core Concepts

### 1. Store
The Store is the core component that manages state and processes intents:

```kotlin
interface NewsStore : Store<NewsStore.Intent, NewsStore.State, NewsStore.Label> {
    
    sealed interface Intent {
        data object LoadNews : Intent
        data class ToggleBookmark(val newsId: String) : Intent
        data class Search(val query: String) : Intent
    }
    
    data class State(
        val news: ImmutableList<NewsResource> = persistentListOf(),
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val error: String? = null
    )
    
    sealed interface Label {
        data class NewsBookmarked(val newsId: String) : Label
        data class ErrorOccurred(val message: String) : Label
    }
}
```

### 2. StoreFactory
Creates Store instances with proper setup:

```kotlin
@Inject
class NewsStoreFactory(
    private val storeFactory: StoreFactory,
    private val newsRepository: NewsRepository,
    private val userDataRepository: UserDataRepository
) {
    fun create(): NewsStore = object : NewsStore, Store<Intent, State, Label> by storeFactory.create(
        name = "NewsStore",
        initialState = State(),
        bootstrapper = BootstrapperImpl(),
        executorFactory = ::ExecutorImpl,
        reducer = ReducerImpl
    ) {}
    
    private sealed interface Action {
        data class NewsLoaded(val news: List<NewsResource>) : Action
        data object NewsLoading : Action
        data class NewsLoadingFailed(val error: Throwable) : Action
    }
    
    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                newsRepository.getNewsResources()
                    .onStart { dispatch(Action.NewsLoading) }
                    .catch { dispatch(Action.NewsLoadingFailed(it)) }
                    .collect { dispatch(Action.NewsLoaded(it)) }
            }
        }
    }
    
    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.LoadNews -> loadNews()
                is Intent.ToggleBookmark -> toggleBookmark(intent.newsId)
                is Intent.Search -> updateSearch(intent.query)
            }
        }
        
        private fun toggleBookmark(newsId: String) {
            scope.launch {
                userDataRepository.toggleBookmark(newsId)
                publish(Label.NewsBookmarked(newsId))
            }
        }
    }
    
    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.NewsLoaded -> copy(news = msg.news, isLoading = false)
            is Message.Loading -> copy(isLoading = true)
            is Message.Error -> copy(error = msg.message, isLoading = false)
            is Message.UpdateSearch -> copy(searchQuery = msg.query)
        }
    }
}
```

### 3. Integration with Compose UI

#### ViewModel Integration
```kotlin
@HiltViewModel
class NewsViewModel @Inject constructor(
    storeFactory: NewsStoreFactory
) : ViewModel() {
    
    private val store = storeFactory.create()
    
    val state: StateFlow<NewsStore.State> = store.states
    val label: Flow<NewStore.Label> = store.labels
    
    fun onIntent(intent: NewsStore.Intent) {
        store.accept(intent)
    }
    
    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}
```

#### Compose Screen
```kotlin
@Composable
fun NewsScreen(
    viewModel: NewsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.labels.collectInLaunchedEffectWithLifecycle {
        when (it) {
            Label.NewsBookmarked -> {
                
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onIntent(Init)
    }
    
    NewsContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}
```

## Module Setup

### 1. Feature Module Dependencies
Add to your feature module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    
    // MVIKotlin
    implementation(libs.mvikotlin)
    implementation(libs.mvikotlin.main)
    implementation(libs.mvikotlin.coroutines)
    
    // Testing
    testImplementation(libs.mvikotlin.test)
}
```

## Testing

### 1. Store Testing
```kotlin
class NewsStoreTest {
    
    @Test
    fun `when LoadNews intent then news is loaded`() = runTest {
        val newsRepository = TestNewsRepository()
        val testNews = listOf(testNewsResource1, testNewsResource2)
        newsRepository.sendNewsResources(testNews)
        
        val store = NewsStoreFactory(
            storeFactory = TestStoreFactory(),
            newsRepository = newsRepository,
            userDataRepository = TestUserDataRepository()
        ).create()
        
        store.states.test {
            assertEquals(
                NewsStore.State(news = testNews, isLoading = false),
                awaitItem()
            )
        }
    }
}
```

### 2. Integration Testing
```kotlin
@Test
fun `news screen displays loaded news`() {
    composeTestRule.setContent {
        NewsScreen(
            viewModel = NewsViewModel(
                storeFactory = createTestStoreFactory()
            )
        )
    }
    
    composeTestRule
        .onNodeWithTag("news_list")
        .assertIsDisplayed()
}
```

## Migration Strategy

### Gradual Migration
1. **New Features**: Implement new features using MVIKotlin
2. **Existing Features**: Keep existing MVVM implementation
3. **Shared Components**: Create adapters for interoperability

## Best Practices

### 1. State Management
- Keep state immutable and data classes
- Use sealed classes for intents and labels
- Avoid complex logic in reducers

### 2. Side Effects
- Handle side effects in Executors
- Use Labels for one-time events
- Keep UI logic in Compose components

### 3. Testing
- Test Stores in isolation
- Use TestStoreFactory for unit tests
- Mock repositories, not stores

### 4. Performance
- Use `distinctUntilChanged()` for state flows
- Implement proper state comparison
- Dispose stores in ViewModel.onCleared()

## Common Patterns

### 1. Loading States
```kotlin
private fun CoroutineExecutor<*, *, State, Message, *>.loadData() {
    scope.launch {
        dispatch(Message.Loading)
        repository.getData()
            .fold(
                onSuccess = { dispatch(Message.DataLoaded(it)) },
                onFailure = { dispatch(Message.Error(it.message)) }
            )
    }
}
```

### 2. Form Validation
```kotlin
sealed interface Intent {
    data class UpdateEmail(val email: String) : Intent
    data object Submit : Intent
}

private fun State.validate(): ValidationResult =
    when {
        email.isBlank() -> ValidationResult.Error("Email is required")
        !email.isValidEmail() -> ValidationResult.Error("Invalid email")
        else -> ValidationResult.Valid
    }
```

## Debugging

### Enable Logging
```kotlin
@Provides
fun provideStoreFactory(): StoreFactory =
    if (BuildConfig.DEBUG) {
        LoggingStoreFactory(DefaultStoreFactory())
    } else {
        DefaultStoreFactory()
    }
```

## Resources

- [MVIKotlin Documentation](https://arkivanov.github.io/MVIKotlin/)
- [MVI Pattern Guide](https://hannesdorfmann.com/android/mosby3-mvi-1)
- [Now in Android Architecture](https://github.com/android/nowinandroid/blob/main/docs/ArchitectureLearningJourney.md)