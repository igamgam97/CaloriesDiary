plugins {
    alias(libs.plugins.caloriesdiary.android.feature)
    alias(libs.plugins.caloriesdiary.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.feature.parameters"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.root)
}