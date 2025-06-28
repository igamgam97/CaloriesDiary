package com.example.caloriesdiary.feature.newmeal.presentation

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NewMealViewModel @Inject constructor(
    storeFactory: NewMealStoreFactory,
) : ViewModel() {

    private val store = storeFactory.create()

    val state: StateFlow<NewMealState> = store.stateFlow
    val labels: Flow<NewMealLabel> = store.labels

    fun onIntent(intent: NewMealIntent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}