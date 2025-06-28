plugins {
    alias(libs.plugins.caloriesdiary.android.feature)
    alias(libs.plugins.caloriesdiary.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.feature.summary"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.root)
}