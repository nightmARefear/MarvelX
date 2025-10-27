package com.alexgreece45.marvelx.util.extensions

import java.time.LocalDateTime
import java.time.OffsetDateTime

fun LocalDateTime.toOffsetDateTime(): OffsetDateTime = OffsetDateTime.parse(this.toString())