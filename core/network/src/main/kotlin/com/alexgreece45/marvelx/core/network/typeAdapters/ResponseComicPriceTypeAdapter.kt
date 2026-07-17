package com.alexgreece45.marvelx.core.network.typeAdapters

import com.alexgreece45.marvelx.core.network.model.api.character.comic.ResponseComicPriceType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

internal class ResponseComicPriceTypeAdapter : TypeAdapter<ResponseComicPriceType>() {

    override fun write(out: JsonWriter, value: ResponseComicPriceType?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.name)
        }
    }

    override fun read(input: JsonReader): ResponseComicPriceType =
        ResponseComicPriceType.getType(input.nextName())
}