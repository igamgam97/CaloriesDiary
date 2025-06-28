package com.example.caloriesdiary.feature.newmeal.presentation

import android.content.Context
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.caloriesdiary.core.data.repository.food.FoodRepository
import com.example.caloriesdiary.core.model.data.Food
import com.example.caloriesdiary.feature.newmeal.R
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.Close
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.Init
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.SaveEntry
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateCalories
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateCarbs
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateFats
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateFoodName
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealIntent.UpdateProteins
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealLabel.NavigateBack
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.CaloriesUpdated
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.CarbsUpdated
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.Error
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.FatsUpdated
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.FoodNameUpdated
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.Loading
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.ProteinsUpdated
import com.example.caloriesdiary.feature.newmeal.presentation.NewMealStoreFactory.Message.ValidationErrorsUpdated
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewMealStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val foodRepository: FoodRepository,
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val MAX_INT_LENGTH = 9
    }

    fun create(): NewMealStore =
        object :
            NewMealStore,
            Store<NewMealIntent, NewMealState, NewMealLabel> by storeFactory.create(
                name = "NewMealStore",
                initialState = NewMealState(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Message {
        data class FoodNameUpdated(val name: String) : Message
        data class CarbsUpdated(val carbs: String) : Message
        data class ProteinsUpdated(val proteins: String) : Message
        data class FatsUpdated(val fats: String) : Message
        data class CaloriesUpdated(val calories: String) : Message
        data class ValidationErrorsUpdated(
            val nameError: String?,
            val carbsError: String?,
            val proteinsError: String?,
            val fatsError: String?,
            val caloriesError: String?,
        ) : Message

        data object Loading : Message
        data class Error(val message: String) : Message
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<NewMealIntent, Nothing, NewMealState, Message, NewMealLabel>() {

        override fun executeIntent(intent: NewMealIntent) {
            when (intent) {
                is Init -> {
                }

                is UpdateFoodName -> {
                    dispatch(FoodNameUpdated(intent.name))
                }

                is UpdateCarbs -> {
                    dispatch(CarbsUpdated(intent.carbs.filterDigitsAndLength()))
                }

                is UpdateProteins -> {
                    dispatch(ProteinsUpdated(intent.proteins.filterDigitsAndLength()))
                }

                is UpdateFats -> {
                    dispatch(FatsUpdated(intent.fats.filterDigitsAndLength()))
                }

                is UpdateCalories -> {
                    dispatch(CaloriesUpdated(intent.calories.filterDigitsAndLength()))
                }

                is SaveEntry -> saveEntry()
                is Close -> publish(NavigateBack)
            }
        }

        private fun String.filterDigitsAndLength(): String {
            return this.filter { it.isDigit() }.take(MAX_INT_LENGTH)
        }

        private fun saveEntry() {
            scope.launch {
                dispatch(Loading)
                try {
                    val validationErrors = validateFields()
                    if (validationErrors.hasErrors()) {
                        dispatch(validationErrors)
                        return@launch
                    }

                    val foodEntry = createFoodEntry()
                    foodRepository.insertFood(foodEntry)
                    publish(NavigateBack)
                } catch (e: Exception) {
                    handleSaveError(e)
                }
            }
        }

        private fun validateFields(): ValidationErrorsUpdated {
            val currentState = state()
            val name = currentState.foodName.value.trim()
            val carbsText = currentState.carbs.value.trim()
            val proteinsText = currentState.proteins.value.trim()
            val fatsText = currentState.fats.value.trim()
            val caloriesText = currentState.calories.value.trim()

            return ValidationErrorsUpdated(
                nameError = validateName(name),
                carbsError = validateRequired(carbsText),
                proteinsError = validateRequired(proteinsText),
                fatsError = validateRequired(fatsText),
                caloriesError = validateRequired(caloriesText),
            )
        }

        private fun validateName(name: String): String? {
            return when {
                name.isEmpty() -> context.getString(R.string.feature_newmeal_error_required)
                name.length < 2 -> context.getString(R.string.feature_newmeal_error_name_length)
                else -> null
            }
        }

        private fun validateRequired(text: String): String? {
            return if (text.isEmpty()) {
                context.getString(R.string.feature_newmeal_error_required)
            } else {
                null
            }
        }

        private fun ValidationErrorsUpdated.hasErrors(): Boolean {
            return nameError != null ||
                carbsError != null ||
                proteinsError != null ||
                fatsError != null ||
                caloriesError != null
        }

        private fun createFoodEntry(): Food {
            val currentState = state()
            return Food(
                name = currentState.foodName.value.trim(),
                calories = currentState.calories.value.trim().toFloatOrNull() ?: 0f,
                protein = currentState.proteins.value.trim().toFloatOrNull() ?: 0f,
                carbs = currentState.carbs.value.trim().toFloatOrNull() ?: 0f,
                fats = currentState.fats.value.trim().toFloatOrNull() ?: 0f,
                timestamp = System.currentTimeMillis(),
            )
        }

        private fun handleSaveError(e: Exception) {
            val errorMessage = context.getString(
                R.string.feature_newmeal_error_save_failed,
                e.message ?: "",
            )
            dispatch(Error(errorMessage))
            publish(
                NewMealLabel.ShowError(
                    context.getString(R.string.feature_newmeal_error_save_general),
                ),
            )
        }
    }

    private object ReducerImpl : Reducer<NewMealState, Message> {
        override fun NewMealState.reduce(msg: Message): NewMealState =
            when (msg) {
                is FoodNameUpdated -> copy(foodName = FieldUiModel(msg.name))
                is CarbsUpdated -> copy(carbs = FieldUiModel(msg.carbs))
                is ProteinsUpdated -> copy(proteins = FieldUiModel(msg.proteins))
                is FatsUpdated -> copy(fats = FieldUiModel(msg.fats))
                is CaloriesUpdated -> copy(calories = FieldUiModel(msg.calories))
                is ValidationErrorsUpdated -> handleError(msg)
                is Loading -> copy(isLoading = true)
                is Error -> copy(isLoading = false)
            }

        private fun NewMealState.handleError(
            validationErrorsUpdated: ValidationErrorsUpdated,
        ): NewMealState {
            return copy(
                foodName = foodName.copy(error = validationErrorsUpdated.nameError),
                carbs = carbs.copy(error = validationErrorsUpdated.carbsError),
                proteins = proteins.copy(error = validationErrorsUpdated.proteinsError),
                fats = fats.copy(error = validationErrorsUpdated.fatsError),
                calories = calories.copy(error = validationErrorsUpdated.caloriesError),
                isLoading = false,
            )
        }
    }
}