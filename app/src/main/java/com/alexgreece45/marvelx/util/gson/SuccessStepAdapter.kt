package com.alexgreece45.marvelx.util.gson

import com.alexgreece45.marvelx.data.models.user.SuccessStep
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class SuccessStepAdapter : TypeAdapter<SuccessStep>() {
    override fun write(out: JsonWriter, value: SuccessStep?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }
    override fun read(input: JsonReader): SuccessStep = SuccessStep.of(input.nextString())
}