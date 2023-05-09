package com.alexgreece45.marvelx.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface ResourceProvider {
    fun getContext(): Context

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String
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
}