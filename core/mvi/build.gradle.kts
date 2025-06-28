plugins {
    alias(libs.plugins.caloriesdiary.android.library)
    alias(libs.plugins.caloriesdiary.hilt)
}

android {
    namespace = "com.example.caloriesdiary.core.mvi"
}

dependencies {
    api(projects.core.designsystem)

    implementation(libs.mvikotlin.main)
    implementation(libs.mvikotlin)
}
