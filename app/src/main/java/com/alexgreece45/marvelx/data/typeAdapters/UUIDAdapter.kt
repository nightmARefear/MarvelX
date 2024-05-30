package com.alexgreece45.marvelx.data.typeAdapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.UUID

class UUIDAdapter : TypeAdapter<UUID>() {

    override fun write(out: JsonWriter, value: UUID?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    override fun read(input: JsonReader): UUID = UUID.fromString(input.nextString())
}