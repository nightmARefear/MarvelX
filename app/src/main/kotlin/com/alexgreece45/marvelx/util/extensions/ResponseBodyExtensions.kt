package com.alexgreece45.marvelx.util.extensions

import okhttp3.ResponseBody

fun ResponseBody.toByteArray() = this.bytes()
