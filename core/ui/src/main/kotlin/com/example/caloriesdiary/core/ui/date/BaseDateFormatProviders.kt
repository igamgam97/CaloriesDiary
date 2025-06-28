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

object BaseDateFormatProviders {

    val FORMAT_DD_MM_YYYY_HH_MM_PROVIDER = DateFormatProvider(
        pattern = "dd.MM.yyyy HH:mm",
    )

    val FORMAT_DD_MM_YYYY_PROVIDER = DateFormatProvider(
        pattern = "dd.MM.yyyy",
    )

    val FORMAT_EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ = DateFormatProvider(
        pattern = "EEE, dd MMM yyyy HH:mm:ss zzz",
    )

    val FORMAT_YYYY_MM_DD_T_HH_MM_SS_SSSZ = DateFormatProvider(
        pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX",
    )

    val FORMAT_DD_MMMM_yyyy_PROVIDER = DateFormatProvider(
        pattern = "dd MMMM yyyy",
    )

    val FORMAT_HH_mm_PROVIDER = DateFormatProvider(
        pattern = "HH:mm",
    )

    val FORMAT_D_MMMM_yyyy_HH_mm_PROVIDER = DateFormatProvider(
        pattern = "d MMMM yyyy, HH:mm",
    )

    val FORMAT_D_MMMM_PROVIDER = DateFormatProvider(
        pattern = "d MMMM",
    )

    val FORMAT_D_PROVIDER = DateFormatProvider(
        pattern = "d",
    )

    val FORMAT_D_MMMM_YYYY_PROVIDER = DateFormatProvider(
        pattern = "d MMMM yyyy",
    )
}