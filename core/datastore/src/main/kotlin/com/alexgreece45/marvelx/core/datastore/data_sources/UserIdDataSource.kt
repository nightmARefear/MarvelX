package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import com.alexgreece45.marvelx.core.datastore.providers.model.UserIdDataStore
import com.alexgreece45.marvelx.core.datastore.extensions.getData
import com.alexgreece45.marvelx.core.datastore.extensions.removeDataCatchable
import com.alexgreece45.marvelx.core.datastore.extensions.updateDataCatchable
import com.alexgreece45.marvelx.core.datastore.mappers.toModel
import com.alexgreece45.marvelx.core.datastore.model.UserIdEntity
import org.koin.core.annotation.Single

interface UserIdDataSource {
    suspend fun getUserId(): String?
    suspend fun setUserId(userId: String)
    suspend fun removeUserId()
}

@Single
internal class UserIdDataSourceImpl(
    @UserIdDataStore private val userIdDataStore: DataStore<UserIdEntity?>
) : UserIdDataSource {

    override suspend fun getUserId(): String? = userIdDataStore.getData()?.toModel()?.value

    override suspend fun setUserId(userId: String) {
        userIdDataStore.updateDataCatchable { _ -> UserIdEntity(userId) }
    }

    override suspend fun removeUserId() {
        userIdDataStore.removeDataCatchable()
    }
}