package com.alexgreece45.marvelx.ui.model

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

        /**
         * Returns the corresponding [Gender] based on the given [value]. If no match was found,
         * [NoSuchElementException] is thrown.
         */
        fun of(value: String): Gender = asList().first { it.key == value }

        fun isValidGender(value: String): Boolean = asList().firstOrNull { it.key == value } != null
    }
}