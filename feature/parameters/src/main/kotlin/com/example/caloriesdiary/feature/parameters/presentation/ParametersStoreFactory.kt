package com.example.caloriesdiary.feature.parameters.presentation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.caloriesdiary.core.data.repository.UserDataRepository
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Intent
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.Label
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStore.State
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStoreFactory.Message.AgeNegativeError
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStoreFactory.Message.HeightNegativeError
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStoreFactory.Message.UserInfoUpdated
import com.example.caloriesdiary.feature.parameters.presentation.ParametersStoreFactory.Message.WeightNegativeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ParametersStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val userDataRepository: UserDataRepository,
) {

    companion object {
        private const val DEBOUNCE_DELAY_MS = 500L
        private const val MAX_INT_LENGTH = 9
    }

    val isInitialized = AtomicBoolean(false)

    private enum class ParameterType {
        HEIGHT,
        WEIGHT,
        AGE,
    }

    fun create(): ParametersStore =
        object :
            ParametersStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "ParametersStore",
                initialState = State(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Message {
        data class UserInfoUpdated(
            val height: String,
            val weight: String,
            val age: String,
        ) : Message

        data object HeightNegativeError : Message
        data object WeightNegativeError : Message
        data object AgeNegativeError : Message
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<Intent, Nothing, State, Message, Label>() {

        private val debounceJobs = mutableMapOf<ParameterType, Job?>()

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.Init -> init()
                is Intent.UpdateHeight -> updateParameter(
                    paramType = ParameterType.HEIGHT,
                    value = intent.height,
                    onSave = userDataRepository::setHeight,
                )

                is Intent.UpdateWeight -> updateParameter(
                    paramType = ParameterType.WEIGHT,
                    value = intent.weight,
                    onSave = userDataRepository::setWeight,
                )

                is Intent.UpdateAge -> updateParameter(
                    paramType = ParameterType.AGE,
                    value = intent.age,
                    onSave = userDataRepository::setAge,
                )
            }
        }

        private fun init() {
            if (isInitialized.getAndSet(true)) return
            scope.launch {
                userDataRepository.userData
                    .distinctUntilChanged()
                    .collect { userData ->
                        dispatch(
                            UserInfoUpdated(
                                height = userData.height.toString(),
                                weight = userData.weight.toString(),
                                age = userData.age.toString(),
                            ),
                        )
                    }
            }
        }

        private fun updateParameter(
            paramType: ParameterType,
            value: String,
            onSave: suspend (Int) -> Unit,
        ) {
            // Only allow numeric input and limit length
            val filteredValue = value.filter { it.isDigit() }.take(MAX_INT_LENGTH)

            // Cancel previous debounce job for this parameter
            debounceJobs[paramType]?.cancel()

            // Update UI immediately with filtered value
            val updatedState = when (paramType) {
                ParameterType.HEIGHT -> UserInfoUpdated(
                    height = filteredValue,
                    weight = state().weight,
                    age = state().age,
                )

                ParameterType.WEIGHT -> UserInfoUpdated(
                    height = state().height,
                    weight = filteredValue,
                    age = state().age,
                )

                ParameterType.AGE -> UserInfoUpdated(
                    height = state().height,
                    weight = state().weight,
                    age = filteredValue,
                )
            }
            dispatch(updatedState)

            // Save to repository with debounce
            debounceJobs[paramType] = scope.launch {
                delay(DEBOUNCE_DELAY_MS)
                val intValue = filteredValue.toIntOrNull() ?: 0
                onSave(intValue)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State =
            when (msg) {
                is UserInfoUpdated -> copy(
                    height = msg.height,
                    weight = msg.weight,
                    age = msg.age,
                )

                is HeightNegativeError -> this
                is WeightNegativeError -> this
                is AgeNegativeError -> this
            }
    }
}