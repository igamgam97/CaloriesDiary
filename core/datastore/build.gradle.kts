plugins {
    alias(libs.plugins.caloriesdiary.android.library)

    alias(libs.plugins.caloriesdiary.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.example.caloriesdiary.core.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.datastoreProto)
    api(projects.core.model)

    implementation(projects.core.common)
}
