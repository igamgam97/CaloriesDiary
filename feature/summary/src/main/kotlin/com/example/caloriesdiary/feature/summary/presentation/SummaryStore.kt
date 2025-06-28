package com.example.caloriesdiary.feature.summary.presentation

import com.arkivanov.mvikotlin.core.store.Store
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Intent
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Label
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.State

interface SummaryStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object Init : Intent
    }

    data class State(
        val summaryData: SummaryData? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    sealed interface Label {
        data class ShowError(val message: String) : Label
    }
}