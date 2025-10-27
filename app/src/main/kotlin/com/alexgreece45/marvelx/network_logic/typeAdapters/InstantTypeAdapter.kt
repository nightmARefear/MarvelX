package com.alexgreece45.marvelx.network_logic.typeAdapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.Instant

class InstantTypeAdapter : TypeAdapter<Instant>() {
    override fun write(out: JsonWriter, value: Instant?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString().replace("\"", ""))
        }
    }

    override fun read(input: JsonReader): Instant =
        Instant.parse(input.nextString())
}