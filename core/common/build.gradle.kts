plugins {
    alias(libs.plugins.caloriesdiary.jvm.library)
    alias(libs.plugins.caloriesdiary.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}