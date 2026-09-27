package com.alexgreece45.marvelx.core.datastore.serializers.data_store

import androidx.datastore.core.Serializer
import com.alexgreece45.marvelx.core.datastore.model.PendingAuthorizationEntity
import com.alexgreece45.marvelx.core.datastore.serializers.model.PendingAuthorizationSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.annotation.Single
import java.io.InputStream
import java.io.OutputStream

@Single
@PendingAuthorizationSerializer
internal class PendingAuthorizationEntitySerializer(
    private val json: Json
) : Serializer<PendingAuthorizationEntity?> {

    override val defaultValue: PendingAuthorizationEntity? = null

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): PendingAuthorizationEntity? {
        return try {
            json.decodeFromStream<PendingAuthorizationEntity>(input)
        } catch (_: Exception) {
            defaultValue
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(
        t: PendingAuthorizationEntity?,
        output: OutputStream
    ) {
        if (t == null) {
            output.write(ByteArray(0))
        } else {
            json.encodeToStream(t, output)
        }
    }
}
