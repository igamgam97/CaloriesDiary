plugins {
    alias(libs.plugins.caloriesdiary.android.library)
    alias(libs.plugins.caloriesdiary.android.library.compose)
}

android {
    namespace = "com.example.caloriesdiary.core.designsystem"
}

dependencies {
    /*lintPublish(projects.lint)*/

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)

    api(libs.kotlinx.immutable)

    implementation(libs.coil.kt.compose)
}
