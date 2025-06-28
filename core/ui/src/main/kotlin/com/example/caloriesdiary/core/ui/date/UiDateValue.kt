/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.core.ui.date

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