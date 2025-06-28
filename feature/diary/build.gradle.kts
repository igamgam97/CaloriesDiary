plugins {
    alias(libs.plugins.caloriesdiary.android.feature)
    alias(libs.plugins.caloriesdiary.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.feature.diary"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(libs.kotlinx.immutable)
    implementation(projects.libs.paging)
    implementation(libs.kotlinx.datetime)
    implementation(projects.core.root)
}