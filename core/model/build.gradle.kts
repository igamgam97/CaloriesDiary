plugins {
    alias(libs.plugins.caloriesdiary.jvm.library)
}

dependencies {
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.immutable)
}
