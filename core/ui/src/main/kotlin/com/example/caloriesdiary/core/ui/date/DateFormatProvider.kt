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