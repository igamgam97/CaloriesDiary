plugins {
    alias(libs.plugins.caloriesdiary.android.library)
    alias(libs.plugins.caloriesdiary.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.core.root"
}

dependencies {
    api(projects.core.designsystem)
}
