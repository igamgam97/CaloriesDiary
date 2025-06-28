plugins {
    alias(libs.plugins.caloriesdiary.android.library)
    alias(libs.plugins.caloriesdiary.android.library.compose)

}

android {
    namespace = "com.example.caloriesdiary.core.ui"
}

dependencies {
    api(projects.core.designsystem)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.kotlinx.datetime)
}
