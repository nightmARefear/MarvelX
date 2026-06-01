package com.alexgreece45.marvelx.core.network.typeAdapters

import com.alexgreece45.marvelx.core.network.model.api.verification_step.ResponseVerificationStep
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

internal class ResponseVerificationStepAdapter : TypeAdapter<ResponseVerificationStep>() {
    override fun write(out: JsonWriter, value: ResponseVerificationStep?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.key)
        }
    }

    override fun read(input: JsonReader): ResponseVerificationStep =
        ResponseVerificationStep.of(input.nextString())
}