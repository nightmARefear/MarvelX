package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.data.api.ApiClient
import javax.inject.Inject

interface UserRepository {
    suspend fun
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient
) : UserRepository {

}