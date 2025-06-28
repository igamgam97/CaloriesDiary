package com.example.caloriesdiary.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val niaDispatcher: CaloriesDiaryDispatchers)

enum class CaloriesDiaryDispatchers {
    Default,
    IO,
}
