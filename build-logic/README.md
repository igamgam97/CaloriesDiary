# Convention Plugins

The `build-logic` folder defines project-specific convention plugins, used to keep a single
source of truth for common module configurations.

Current list of convention plugins:

- [`caloriesdiary.android.application`](convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt):
  Configures common Android application and Kotlin options.
- [`caloriesdiary.android.library`](convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt):
  Configures common Android library and Kotlin options.
- [`caloriesdiary.android.test`](convention/src/main/kotlin/AndroidTestConventionPlugin.kt):
  Configures Android test modules.
- [`caloriesdiary.android.application.compose`](convention/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt):
  Configures Jetpack Compose for Android applications.
- [`caloriesdiary.android.library.compose`](convention/src/main/kotlin/AndroidLibraryComposeConventionPlugin.kt):
  Configures Jetpack Compose for Android libraries.
- [`caloriesdiary.android.feature`](convention/src/main/kotlin/AndroidFeatureConventionPlugin.kt):
  Configures common settings for feature modules (includes library, Hilt).
- [`caloriesdiary.android.application.flavors`](convention/src/main/kotlin/AndroidApplicationFlavorsConventionPlugin.kt):
  Configures product flavors for Android applications.
- [`caloriesdiary.android.lint`](convention/src/main/kotlin/AndroidLintConventionPlugin.kt):
  Configures lint options for Android modules.
- [`caloriesdiary.android.room`](convention/src/main/kotlin/AndroidRoomConventionPlugin.kt):
  Configures Room database dependencies and KSP.
- [`caloriesdiary.hilt`](convention/src/main/kotlin/HiltConventionPlugin.kt):
  Configures Hilt dependency injection.
- [`caloriesdiary.jvm.library`](convention/src/main/kotlin/JvmLibraryConventionPlugin.kt):
  Configures JVM library modules with Kotlin and lint support.
- [`caloriesdiary.detekt`](convention/src/main/kotlin/DetektConfigPlugin.kt):
  Configures Detekt static analysis with formatting and Compose rules.
