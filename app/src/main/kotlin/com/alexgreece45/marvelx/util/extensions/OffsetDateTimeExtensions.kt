package com.alexgreece45.marvelx.util.extensions

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun OffsetDateTime.toSystemLocalDateTime(): LocalDateTime =
    this.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()