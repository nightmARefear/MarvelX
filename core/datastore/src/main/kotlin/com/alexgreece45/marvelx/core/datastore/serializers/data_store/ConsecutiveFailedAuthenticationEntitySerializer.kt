package com.alexgreece45.marvelx.core.datastore.serializers.data_store

import androidx.datastore.core.Serializer
import com.alexgreece45.marvelx.core.datastore.model.AuthenticationTokensEntity
import com.alexgreece45.marvelx.core.datastore.model.ConsecutiveFailedAuthenticationEntity
import com.alexgreece45.marvelx.core.datastore.serializers.model.ConsecutiveFailedAuthenticationSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.annotation.Single
import java.io.InputStream
import java.io.OutputStream

@Single
@ConsecutiveFailedAuthenticationSerializer
internal class ConsecutiveFailedAuthenticationEntitySerializer(
    private val json: Json
) : Serializer<ConsecutiveFailedAuthenticationEntity?> {

    override val defaultValue: ConsecutiveFailedAuthenticationEntity? = null

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): ConsecutiveFailedAuthenticationEntity? {
        return try {
            json.decodeFromStream<ConsecutiveFailedAuthenticationEntity>(input)
        } catch (_: Exception) {
            defaultValue
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(
        t: ConsecutiveFailedAuthenticationEntity?,
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