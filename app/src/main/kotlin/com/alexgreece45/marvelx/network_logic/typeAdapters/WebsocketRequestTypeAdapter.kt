package com.alexgreece45.marvelx.network_logic.typeAdapters

import com.alexgreece45.marvelx.controllers.models.websocket.WebsocketRequestType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class WebsocketRequestTypeAdapter : TypeAdapter<WebsocketRequestType>() {

    override fun write(out: JsonWriter, value: WebsocketRequestType?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.key)
        }
    }

    override fun read(input: JsonReader): WebsocketRequestType =
        WebsocketRequestType.of(input.nextName())
}