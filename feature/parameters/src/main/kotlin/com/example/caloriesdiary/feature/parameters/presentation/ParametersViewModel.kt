package com.example.caloriesdiary.feature.parameters.presentation

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ParametersViewModel @Inject constructor(
    storeFactory: ParametersStoreFactory,
) : ViewModel() {

    private val store = storeFactory.create()

    val state: StateFlow<State> = store.stateFlow

    fun onIntent(intent: Intent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}