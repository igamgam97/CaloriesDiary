package com.example.caloriesdiary.feature.newmeal.presentation

/**
 * NewMeal Intent definitions for MVIKotlin Store.
 */
sealed interface NewMealIntent {
    data object Init : NewMealIntent
    data class UpdateFoodName(val name: String) : NewMealIntent
    data class UpdateCarbs(val carbs: String) : NewMealIntent
    data class UpdateProteins(val proteins: String) : NewMealIntent
    data class UpdateFats(val fats: String) : NewMealIntent
    data class UpdateCalories(val calories: String) : NewMealIntent
    data object SaveEntry : NewMealIntent
    data object Close : NewMealIntent
}

/**
 * NewMeal State for MVIKotlin Store.
 */
data class NewMealState(
    val foodName: FieldUiModel = FieldUiModel.Empty,
    val carbs: FieldUiModel = FieldUiModel.Empty,
    val proteins: FieldUiModel = FieldUiModel.Empty,
    val fats: FieldUiModel = FieldUiModel.Empty,
    val calories: FieldUiModel = FieldUiModel.Empty,
    val isLoading: Boolean = false,
)

data class FieldUiModel(
    val value: String,
    val error: String? = null,
) {
    companion object {
        val Empty = FieldUiModel(value = "")
    }
}

/**
 * NewMeal Label for one-time events from Store.
 */
sealed interface NewMealLabel {
    data object NavigateBack : NewMealLabel
    data class ShowError(val message: String) : NewMealLabel
}