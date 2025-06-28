package com.example.caloriesdiary.core.ui.date

import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.Format

@JvmInline
value class UiDateValue(
    val value: LocalDateTime,
) {
    companion object {
        val Default = UiDateValue(value = 0L.asLocalDateTime())
    }

    @Composable
    fun string(formatProvider: DateFormatProvider): String {
        // TODO(GlebShcherbakov): использовать kotlinx.datetime.DateTimeFormatter когда появится
        return formatProvider.provide().rememberFormat(value.toJavaLocalDateTime())
    }

    fun string(format: Format): String {
        return format.format(value.toJavaLocalDateTime())
    }
}

@Composable
fun UiDateValue.dateTimeFormatString(): String {
    return this.string(formatProvider = BaseDateFormatProviders.FORMAT_DD_MM_YYYY_PROVIDER)
}

fun LocalDateTime.asUiDate(): UiDateValue = UiDateValue(this)

fun Long.asLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
}

fun Long.asUiDate(): UiDateValue {
    return asLocalDateTime()
        .asUiDate()
}

fun Instant.asUiDate(): UiDateValue {
    return this.toLocalDateTime(TimeZone.currentSystemDefault()).asUiDate()
}

fun LocalDateTime.epochSeconds(): Long {
    return toInstant(TimeZone.UTC).epochSeconds
}