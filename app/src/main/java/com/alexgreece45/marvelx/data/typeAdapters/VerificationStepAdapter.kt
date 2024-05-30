package com.alexgreece45.marvelx.data.typeAdapters

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class VerificationStepAdapter : TypeAdapter<VerificationStep>() {
    override fun write(out: JsonWriter, value: VerificationStep?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.key)
        }
    }

    override fun read(input: JsonReader): VerificationStep = VerificationStep.of(input.nextString())
}