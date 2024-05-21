package com.alexgreece45.marvelx.data.models_api.user

import android.os.Parcelable
import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import kotlinx.parcelize.Parcelize

sealed class Gender(
    val key: String,
    @StringRes val label: Int
) : Parcelable {
    @Parcelize
    data object Female : Gender(
        key = "female",
        label = R.string.female
    )

    @Parcelize
    data object Male : Gender(
        key = "male",
        label = R.string.male
    )

    @Parcelize
    data object Other : Gender(
        key = "other",
        label = R.string.other
    )

    companion object {
        fun asList(): List<Gender> = listOf(Female, Male, Other)

        fun of(value: String): Gender? = asList().firstOrNull { it.key == value }

        fun isStringValidGender(value: String): Boolean = of(value) != null
    }
}