package com.alexgreece45.marvelx.network_logic.util.extensions

import com.google.gson.Gson
import okhttp3.ResponseBody

/**
 * Serializes this [ResponseBody] to [T].
 */
inline fun <reified T : Any> ResponseBody?.toSerialized(): T {
    return Gson().fromJson(
        this?.byteString()?.utf8(),
        T::class.java
    )
}
