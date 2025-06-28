package com.example.caloriesdiary.core.ui.date

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