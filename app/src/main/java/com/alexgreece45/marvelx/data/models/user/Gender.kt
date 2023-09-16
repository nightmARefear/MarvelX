package com.alexgreece45.marvelx.data.models.user

import android.os.Parcelable
import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import kotlinx.parcelize.Parcelize

sealed class Gender(
    val key: String,
    @StringRes val label: Int
) : Parcelable {
    @Parcelize
    object Female : Gender(
        key = "female",
        label = R.string.female
    )

    @Parcelize
    object Male : Gender(
        key = "male",
        label = R.string.male
    )

    @Parcelize
    object Other : Gender(
        key = "other",
        label = R.string.other
    )

    companion object {
        private fun asList() : List<Gender> = listOf(Female, Male, Other)

        fun of(value: String): Gender? = asList().firstOrNull { it.key == value }

        fun isStringValidGender(value: String): Boolean = of(value) != null
    }
}
