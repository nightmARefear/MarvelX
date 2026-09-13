package com.alexgreece45.marvelx.data.repository.marketplace.listing.details

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.network.clients.ListingClient
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.toData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface ListingDetailsRepository {

    fun init()

    val listingStatuses: List<EnumGeneric>
    val listingApparelSizes: List<EnumGeneric>
    val listingMaterials: List<EnumGeneric>
    val listingCollectibleScales: List<EnumGeneric>
    val listingConditions: List<EnumGeneric>
    val listingGrades: List<EnumGeneric>
    val listingCardRarities: List<EnumGeneric>
    val listingCardVariants: List<EnumGeneric>
}

@Single
internal class ListingDetailsRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val appLogger: AppLogger
) : ListingDetailsRepository {

    private val loggerTag = "ListingDetailsRepository"

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private var _cachedListingStatuses: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingApparelSizes: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingMaterials: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingCollectibleScales: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingConditions: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingGrades: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingCardRarities: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingCardVariants: MutableStateFlow<List<EnumGeneric>> =
        MutableStateFlow(emptyList())

    private val operations = listOf(
        ListingEnumOperation(
            apiCall = listingClient::getListingStatuses,
            cachedVariable = _cachedListingStatuses,
            operationName = "statuses"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingApparelSizes,
            cachedVariable = _cachedListingApparelSizes,
            operationName = "apparel sizes"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingCollectibleScales,
            cachedVariable = _cachedListingCollectibleScales,
            operationName = "collectible scales"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingConditions,
            cachedVariable = _cachedListingConditions,
            operationName = "conditions"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingGrades,
            cachedVariable = _cachedListingGrades,
            operationName = "grades"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingMaterials,
            cachedVariable = _cachedListingMaterials,
            operationName = "materials"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingCardRarities,
            cachedVariable = _cachedListingCardRarities,
            operationName = "card rarities"
        ),
        ListingEnumOperation(
            apiCall = listingClient::getListingCardVariants,
            cachedVariable = _cachedListingCardVariants,
            operationName = "card variants"
        )
    )

    override fun init() {
        scope.launch {
            operations.forEach { operation ->
                when (val apiResult = operation.apiCall()) {
                    is ApiResult.Error -> {
                        appLogger.d(loggerTag) { "An error occurred whilst initializing the cached listing ${operation.operationName}.\n" + apiResult.errorBody }
                    }

                    is ApiResult.Failed -> {
                        appLogger.d(loggerTag) { "An error occurred whilst initializing the cached listing ${operation.operationName}.\n" + apiResult.failureMessage }
                    }

                    ApiResult.NetworkError -> {
                        appLogger.d(loggerTag) { "A network error occurred whilst initializing the cached listing ${operation.operationName}." }
                    }

                    is ApiResult.Success -> {
                        try {
                            val listingEnums =
                                apiResult.body.map { it.toData() }.sortedBy { it.sortOrder }
                            operation.cachedVariable.value = listingEnums
                        } catch (e: Exception) {
                            appLogger.d(loggerTag) { "An error occurred whilst initializing the cached listing ${operation.operationName}.\n" + e.localizedMessage }
                        }
                    }
                }
            }
        }
    }

    override val listingStatuses: List<EnumGeneric>
        get() = _cachedListingStatuses.value

    override val listingApparelSizes: List<EnumGeneric>
        get() = _cachedListingApparelSizes.value

    override val listingMaterials: List<EnumGeneric>
        get() = _cachedListingMaterials.value

    override val listingCollectibleScales: List<EnumGeneric>
        get() = _cachedListingCollectibleScales.value

    override val listingConditions: List<EnumGeneric>
        get() = _cachedListingConditions.value

    override val listingGrades: List<EnumGeneric>
        get() = _cachedListingGrades.value

    override val listingCardRarities: List<EnumGeneric>
        get() = _cachedListingCardRarities.value

    override val listingCardVariants: List<EnumGeneric>
        get() = _cachedListingCardVariants.value
}

private data class ListingEnumOperation(
    val apiCall: suspend () -> ApiResult<List<ResponseListingEnumGeneric>>,
    var cachedVariable: MutableStateFlow<List<EnumGeneric>>,
    val operationName: String
)