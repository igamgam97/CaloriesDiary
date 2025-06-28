plugins {
    alias(libs.plugins.caloriesdiary.android.library)
    alias(libs.plugins.caloriesdiary.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.example.caloriesdiary.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(projects.core.datastore)
    api(projects.core.database)
    implementation(projects.core.model)
}
