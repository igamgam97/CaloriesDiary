package com.example.caloriesdiary.feature.diary.component.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caloriesdiary.core.ui.date.UiDateValue
import com.example.caloriesdiary.core.ui.date.asUiDate
import com.example.caloriesdiary.core.ui.date.fullDateTimeFormatString
import kotlinx.datetime.LocalDateTime
import java.util.UUID

@Composable
fun FoodEntryCard(
    foodUiModel: FoodUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(180.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        when (foodUiModel) {
            is FoodUiModel.Data -> {
                FoodEntryCardData(
                    foodUiModel = foodUiModel,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            is FoodUiModel.Placeholder -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth()
                        .background(color = Color(0xFF2A2A2A)),
                )
            }
        }
    }
}

@Composable
fun FoodEntryCardData(
    foodUiModel: FoodUiModel.Data,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = foodUiModel.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${foodUiModel.calories} KCAL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Macronutrients
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            MacronutrientInfo(
                value = foodUiModel.carbs.toInt(),
                label = "CARBS",
            )
            MacronutrientInfo(
                value = foodUiModel.protein.toInt(),
                label = "PROTEINS",
            )
            MacronutrientInfo(
                value = foodUiModel.fats.toInt(),
                label = "FATS",
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = foodUiModel.timestamp.fullDateTimeFormatString(),
            color = Color.Gray,
            fontSize = 12.sp,
        )
    }
}

@Immutable
@JvmInline
value class FoodHistoryItemUiId(val value: Long) {
    companion object {
        val Empty = FoodHistoryItemUiId(0)

        fun random() = FoodHistoryItemUiId(UUID.randomUUID().mostSignificantBits)
    }
}

sealed interface FoodUiModel {

    val id: FoodHistoryItemUiId

    data class Data(
        override val id: FoodHistoryItemUiId,
        val name: String,
        val calories: Float,
        val protein: Float,
        val carbs: Float,
        val fats: Float,
        val timestamp: UiDateValue,
    ) : FoodUiModel

    data class Placeholder(
        override val id: FoodHistoryItemUiId,
    ) : FoodUiModel
}

@Composable
private fun MacronutrientInfo(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Preview
@Composable
fun FoodEntryCardPlaceholderPreview() {
    FoodEntryCard(
        foodUiModel = FoodUiModel.Placeholder(FoodHistoryItemUiId(-1)),
    )
}

@Preview
@Composable
fun FoodEntryCardDataPreview() {
    FoodEntryCardData(
        foodUiModel = FoodUiModel.Data(
            id = FoodHistoryItemUiId(-1),
            name = "Sample Food",
            calories = 250f,
            protein = 20f,
            carbs = 30f,
            fats = 10f,
            timestamp = LocalDateTime(
                year = 2025,
                monthNumber = 12,
                dayOfMonth = 31,
                hour = 23,
                minute = 59,
                second = 59,
            ).asUiDate(),
        ),
    )
}