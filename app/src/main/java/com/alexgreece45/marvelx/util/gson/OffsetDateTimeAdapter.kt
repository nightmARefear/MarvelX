package com.alexgreece45.marvelx.util.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class OffsetDateTimeAdapter : TypeAdapter<OffsetDateTime>() {
    override fun write(out: JsonWriter, value: OffsetDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }
    override fun read(input: JsonReader): OffsetDateTime = OffsetDateTime.parse(input.nextString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"))
}