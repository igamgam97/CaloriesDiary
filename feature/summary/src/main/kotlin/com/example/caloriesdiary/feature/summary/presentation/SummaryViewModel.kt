package com.example.caloriesdiary.feature.summary.presentation

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Intent
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.Label
import com.example.caloriesdiary.feature.summary.presentation.SummaryStore.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    storeFactory: SummaryStoreFactory,
) : ViewModel() {

    private val store = storeFactory.create()

    val state: StateFlow<State> = store.stateFlow
    val label: Flow<Label> = store.labels

    fun onIntent(intent: Intent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}