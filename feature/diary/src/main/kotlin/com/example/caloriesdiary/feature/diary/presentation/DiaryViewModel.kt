package com.example.caloriesdiary.feature.diary.presentation

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Intent
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.Label
import com.example.caloriesdiary.feature.diary.presentation.DiaryStore.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    storeFactory: DiaryStoreFactory,
) : ViewModel() {

    private val store = storeFactory.create()

    val state: StateFlow<State> = store.stateFlow
    val labels: Flow<Label> = store.labels

    fun onIntent(intent: Intent) {
        store.accept(intent)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }
}