package com.example.caloriesdiary.core.ui.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.Format
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DateFormatProvider(
    private val pattern: String,
) {
    fun provide(locale: Locale): Format {
        return DateTimeFormatter.ofPattern(pattern, locale).toFormat()
    }

    @Composable
    fun provide(): Format {
        val locale = Locale.getDefault()
        return remember(pattern, locale) { provide(locale) }
    }

    fun provideDateTimeFormatter(locale: Locale): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(pattern, locale)
    }
}

@Composable
fun Format.rememberFormat(any: Any): String {
    return remember(this, any) { this.format(any) }
}