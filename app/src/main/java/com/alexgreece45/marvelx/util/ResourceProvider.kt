package com.alexgreece45.marvelx.util

import android.content.Context
import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface ResourceProvider {
    fun getContext(): Context

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String

    fun getGenericErrorMessage(): String

    fun getNoInternetConnectionMessage(): String
}

@Singleton
class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    override fun getContext(): Context = context

    override fun getString(resId: Int, vararg formatArgs: Any?): String =
        try {
            context.getString(resId, *formatArgs)
        } catch (_: Exception) {
            ""
        }

    override fun getGenericErrorMessage(): String = getString(R.string.unknown_error)

    override fun getNoInternetConnectionMessage(): String =
        getString(R.string.no_internet_connection_error_message)
}