package com.alexgreece45.marvelx.network_logic.typeAdapters

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkActionType
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

    override fun read(input: JsonReader): DeepLinkActionType {
        val rawValue = input.nextString()
        return when (rawValue) { // TODO: For some reason, DeepLinkActionType.fromName doesn't work.
            DeepLinkActionType.Action.name -> DeepLinkActionType.Action
            DeepLinkActionType.View.name -> DeepLinkActionType.View
            else -> DeepLinkActionType.Invalid(rawValue)
        }
    }
}
