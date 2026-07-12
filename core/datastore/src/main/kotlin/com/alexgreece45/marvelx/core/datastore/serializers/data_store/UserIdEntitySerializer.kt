package com.alexgreece45.marvelx.core.datastore.serializers.data_store

import androidx.datastore.core.Serializer
import com.alexgreece45.marvelx.core.datastore.model.UserIdEntity
import com.alexgreece45.marvelx.core.datastore.serializers.model.UserIdSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.koin.core.annotation.Single
import java.io.InputStream
import java.io.OutputStream

@Single
@UserIdSerializer
internal class UserIdEntitySerializer(
    private val json: Json
) : Serializer<UserIdEntity?> {

    override val defaultValue: UserIdEntity? = null

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): UserIdEntity? {
        return try {
            json.decodeFromStream<UserIdEntity>(input)
        } catch (_: Exception) {
            defaultValue
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(
        t: UserIdEntity?,
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