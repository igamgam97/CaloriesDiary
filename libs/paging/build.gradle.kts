plugins {
    alias(libs.plugins.caloriesdiary.android.library.compose)
    alias(libs.plugins.caloriesdiary.android.library)
}

android {
    namespace = "com.libs.paging"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
}