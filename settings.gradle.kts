pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        val detektVersion = "1.23.5"
        id("io.gitlab.arturbosch.detekt") version detektVersion
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CaloriesDiary"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":libs:paging")
include(":core:designsystem")
include(":core:ui")
include(":core:data")
include(":core:datastore-proto")
include(":core:datastore")
include(":core:common")
include(":core:model")
include(":core:root")
include(":core:mvi")
include(":core:database")
include(":core:domain")
include(":feature:parameters")
