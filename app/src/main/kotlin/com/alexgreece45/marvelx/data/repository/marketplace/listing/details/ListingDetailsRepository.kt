package com.alexgreece45.marvelx.data.repository.marketplace.listing.details

import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingEnumGeneric
import com.alexgreece45.marvelx.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ListingClient
import com.alexgreece45.marvelx.network_logic.models_api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface ListingDetailsRepository {

    fun init()

    val listingStatuses: List<ListingEnumGeneric>
    val listingApparelSizes: List<ListingEnumGeneric>
    val listingMaterials: List<ListingEnumGeneric>
    val listingCollectibleScales: List<ListingEnumGeneric>
    val listingConditions: List<ListingEnumGeneric>
    val listingGrades: List<ListingEnumGeneric>
    val listingCardRarities: List<ListingEnumGeneric>
    val listingCardVariants: List<ListingEnumGeneric>
}

class ListingDetailsRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val appLogger: AppLogger
) : ListingDetailsRepository {

    private val loggerTag = "ListingDetailsRepository"

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private var _cachedListingStatuses: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingApparelSizes: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingMaterials: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingCollectibleScales: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingConditions: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingGrades: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingCardRarities: MutableStateFlow<List<ListingEnumGeneric>> =
        MutableStateFlow(emptyList())

    private var _cachedListingCardVariants: MutableStateFlow<List<ListingEnumGeneric>> =
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
                val apiResult = operation.apiCall()
                when (apiResult) {
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
                            val listingEnums = apiResult.body.map { it.toData() }
                            operation.cachedVariable.value = listingEnums
                        } catch (e: Exception) {
                            appLogger.d(loggerTag) { "An error occurred whilst initializing the cached listing ${operation.operationName}.\n" + e.localizedMessage }
                        }
                    }
                }
            }
        }
    }

    override val listingStatuses: List<ListingEnumGeneric>
        get() = _cachedListingStatuses.value

    override val listingApparelSizes: List<ListingEnumGeneric>
        get() = _cachedListingApparelSizes.value

    override val listingMaterials: List<ListingEnumGeneric>
        get() = _cachedListingMaterials.value

    override val listingCollectibleScales: List<ListingEnumGeneric>
        get() = _cachedListingCollectibleScales.value

    override val listingConditions: List<ListingEnumGeneric>
        get() = _cachedListingConditions.value

    override val listingGrades: List<ListingEnumGeneric>
        get() = _cachedListingGrades.value

    override val listingCardRarities: List<ListingEnumGeneric>
        get() = _cachedListingCardRarities.value

    override val listingCardVariants: List<ListingEnumGeneric>
        get() = _cachedListingCardVariants.value
}

private data class ListingEnumOperation(
    val apiCall: suspend () -> ApiResult<List<ResponseListingEnumGeneric>>,
    var cachedVariable: MutableStateFlow<List<ListingEnumGeneric>>,
    val operationName: String
)
