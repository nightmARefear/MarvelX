package com.alexgreece45.marvelx.network_logic.typeAdapters

import com.alexgreece45.marvelx.ui.helper.model.DeepLinkActionType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class DeepLinkActionTypeTypeAdapter : TypeAdapter<DeepLinkActionType>() {
    override fun write(out: JsonWriter, value: DeepLinkActionType?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.name)
        }
    }

    override fun read(input: JsonReader): DeepLinkActionType =
        DeepLinkActionType.fromName(input.nextString())
}
