package com.alexgreece45.marvelx.core.common.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun LocalDate.toCustomFormat(pattern: String): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.defaultFormat(): String = this.toCustomFormat("MMMM d, yyyy")

fun LocalDate.toInstant(): Instant = this.atStartOfDay(ZoneId.systemDefault()).toInstant()

fun LocalDate.toEpochMillis(): Long = this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()