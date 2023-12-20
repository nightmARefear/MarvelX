package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.models.user.profileDetails.toProfileDetailsDomain
import com.alexgreece45.marvelx.repository.GetProfileDetailsRepositoryResponse
import com.alexgreece45.marvelx.repository.UserRepository
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun getProfileDetails(): Flow<GetProfileDetailsPartialState>
}

class UserInteractorImpl @Inject constructor(
    private val repository: UserRepository,
    private val prefKeys: PrefKeys,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLoggerController
) : UserInteractor {

    override suspend fun getProfileDetails(): Flow<GetProfileDetailsPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.getProfileDetails(prefKeys.getUserId()).collect {
                when (it) {

                    is GetProfileDetailsRepositoryResponse.Failed -> {
                        emit(GetProfileDetailsPartialState.Failed(errorMessage = genericErrorMessage))
                    }

                    is GetProfileDetailsRepositoryResponse.Success -> {
                        it.response?.let { responseProfileDetails ->
                            emit(GetProfileDetailsPartialState.Success(profileDetailsDomain = responseProfileDetails.toProfileDetailsDomain()))
                        }
                            ?: emit(GetProfileDetailsPartialState.Failed(errorMessage = genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            logger.e { "(getProfileDetails) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> GetProfileDetailsPartialState.NoInternetConnection(
                    errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                )

                else -> GetProfileDetailsPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
}