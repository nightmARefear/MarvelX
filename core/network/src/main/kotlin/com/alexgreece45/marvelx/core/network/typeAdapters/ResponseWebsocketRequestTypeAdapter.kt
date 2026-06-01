package com.alexgreece45.marvelx.core.network.typeAdapters

import com.alexgreece45.marvelx.core.network.model.websocket.ResponseWebsocketRequestType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

internal class ResponseWebsocketRequestTypeAdapter : TypeAdapter<ResponseWebsocketRequestType>() {

    override fun write(out: JsonWriter, value: ResponseWebsocketRequestType?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.key)
        }
    }

    override fun read(input: JsonReader): ResponseWebsocketRequestType =
        ResponseWebsocketRequestType.of(input.nextName())
}