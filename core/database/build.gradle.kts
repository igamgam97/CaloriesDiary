plugins {
    alias(libs.plugins.caloriesdiary.android.library)

    alias(libs.plugins.caloriesdiary.android.room)
    alias(libs.plugins.caloriesdiary.hilt)
}

android {
    namespace = "com.example.caloriesdiary.core.database"
}

dependencies {
    api(projects.core.model)

    implementation(libs.kotlinx.datetime)
}
