package com.example.caloriesdiary.feature.parameters.presentation

import com.arkivanov.mvikotlin.core.store.Store
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Label
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.State

interface ParametersStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object Init : Intent
        data class UpdateHeight(val height: String) : Intent
        data class UpdateWeight(val weight: String) : Intent
        data class UpdateAge(val age: String) : Intent
    }

    data class State(
        val height: String = "",
        val weight: String = "",
        val age: String = "",
    )

    sealed interface Label
}