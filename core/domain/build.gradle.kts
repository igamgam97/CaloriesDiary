plugins {
    alias(libs.plugins.caloriesdiary.android.library)

    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.caloriesdiary.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)
}