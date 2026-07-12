package com.alexgreece45.marvelx.core.datastore.serializers.data_store

import androidx.datastore.core.Serializer
import com.alexgreece45.marvelx.core.datastore.model.SignatureEntity
import com.alexgreece45.marvelx.core.datastore.serializers.model.SignatureSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.annotation.Single
import java.io.InputStream
import java.io.OutputStream

@Single
@SignatureSerializer
internal class SignatureEntitySerializer(
    private val json: Json
) : Serializer<SignatureEntity?> {

    override val defaultValue: SignatureEntity? = null

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): SignatureEntity? {
        return try {
            json.decodeFromStream<SignatureEntity>(input)
        } catch (_: Exception) {
            defaultValue
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(
        t: SignatureEntity?,
        output: OutputStream
    ) {
        try {
            if (t == null) {
                output.write(ByteArray(0))
            } else {
                json.encodeToStream(t, output)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}