package com.example.caloriesdiary.core.ui.date

import androidx.compose.runtime.Composable

@Composable
fun UiDateValue.fullDateTimeFormatString(): String {
    return this.string(formatProvider = BaseDateFormatProviders.FORMAT_DD_MM_YYYY_HH_MM_PROVIDER)
}