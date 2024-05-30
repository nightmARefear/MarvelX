package com.alexgreece45.marvelx.data.typeAdapters

import com.alexgreece45.marvelx.domain.models_api.comic.ComicPriceType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ComicPriceTypeAdapter : TypeAdapter<ComicPriceType>() {

    override fun write(out: JsonWriter, value: ComicPriceType?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.name)
        }
    }

    override fun read(input: JsonReader): ComicPriceType = ComicPriceType.getType(input.nextName())
}