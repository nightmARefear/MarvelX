package com.alexgreece45.marvelx.util

import android.content.Context
import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

interface ResourceProvider {
    fun getContext(): Context

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String

    fun getGenericErrorMessage(): String

    fun getNoInternetConnectionMessage(): String
}

class ResourceProviderImpl(
    private val context: Context
) : ResourceProvider {
    override fun getContext(): Context = context

    override fun getString(resId: Int, vararg formatArgs: Any?): String =
        try {
            context.getString(resId, *formatArgs)
        } catch (_: Exception) {
            ""
        }

    override fun getGenericErrorMessage(): String = getString(R.string.generic_error)

    override fun getNoInternetConnectionMessage(): String =
        getString(R.string.no_internet_connection_error_message)
}