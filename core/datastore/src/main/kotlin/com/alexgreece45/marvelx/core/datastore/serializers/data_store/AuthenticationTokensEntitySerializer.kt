package com.alexgreece45.marvelx.core.datastore.serializers.data_store

import androidx.datastore.core.Serializer
import com.alexgreece45.marvelx.core.datastore.model.AuthenticationTokensEntity
import com.alexgreece45.marvelx.core.datastore.serializers.model.AuthenticationTokensSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.annotation.Single
import java.io.InputStream
import java.io.OutputStream

@Single
@AuthenticationTokensSerializer
internal class AuthenticationTokensEntitySerializer(
    private val json: Json
) : Serializer<AuthenticationTokensEntity?> {

    override val defaultValue: AuthenticationTokensEntity? = null

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): AuthenticationTokensEntity? {
        return try {
            json.decodeFromStream<AuthenticationTokensEntity>(input)
        } catch (_: Exception) {
            defaultValue
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(
        t: AuthenticationTokensEntity?,
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