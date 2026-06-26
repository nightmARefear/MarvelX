package com.alexgreece45.marvelx.data.repository.marketplace.listing

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResult
import com.alexgreece45.marvelx.core.network.clients.ListingClient
import com.alexgreece45.marvelx.data.model.marketplace.listing.ListingCategory
import com.alexgreece45.marvelx.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.database.ListingCategoryDao
import com.alexgreece45.marvelx.database.ListingCategoryWithSubcategoriesEntity
import com.alexgreece45.marvelx.database.toDatabase
import com.alexgreece45.marvelx.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface ListingCategoryRepository {

    fun init()

    /**
     * Retrieves the listing categories.
     */
    val listingCategories: List<ListingCategory>
}

@Single
class ListingCategoryRepositoryImpl(
    private val listingClient: ListingClient,
    private val listingCategoryDao: ListingCategoryDao,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val appLogger: AppLogger
) : ListingCategoryRepository {

    private val loggerTag = "ListingCategoryRepository"

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private var _cachedCategories: MutableStateFlow<List<ListingCategory>> =
        MutableStateFlow(emptyList())

    init {
        appLogger.d(loggerTag) { "Initialized. ${DateUtil.getUTCInstant()}" }
    }

    override fun init() {
        scope.launch {
            when (val cachedCategoriesResult = getCachedCategories()) {
                is RepositoryResult.Failed, RepositoryResult.NetworkError -> {
                    appLogger.d(loggerTag) { "An error occurred whilst retrieving the cached listing categories." }

                    when (val repositoryResult = getListingCategories()) {
                        is RepositoryResult.Failed -> {
                            appLogger.d(loggerTag) { "An error occurred whilst initializing the cached listing categories.\n" + repositoryResult.failureMessage }
                        }

                        RepositoryResult.NetworkError -> {
                            appLogger.d(loggerTag) { "A network error occurred whilst initializing the cached listing categories." }
                        }

                        is RepositoryResult.Success -> {
                            // Update database first.
                            saveListingCategoriesToDatabase(repositoryResult.data)

                            _cachedCategories.value = repositoryResult.data
                        }
                    }
                }

                is RepositoryResult.Success -> {
                    if (cachedCategoriesResult.data.isEmpty()) {
                        val listingCategories = getListingCategoriesAndSaveToDatabase()
                        listingCategories?.let {
                            _cachedCategories.value = it
                        }
                    } else {
                        _cachedCategories.value = cachedCategoriesResult.data
                    }
                }
            }
        }
    }

    override val listingCategories: List<ListingCategory> get() = _cachedCategories.value

    private suspend fun getCachedCategories(): RepositoryResult<List<ListingCategory>> =
        try {
            val categories = listingCategoryDao.getAllCategories()
            val subcategories = listingCategoryDao.getAllSubcategories()
            val categoriesAndSubcategories = categories.map { categoryEntity ->
                ListingCategoryWithSubcategoriesEntity(
                    category = categoryEntity,
                    subcategories = subcategories.filter { subcategoryEntity -> subcategoryEntity.categoryId == categoryEntity.id }
                )
            }
            RepositoryResult.Success(categoriesAndSubcategories.map { it.toData() })
        } catch (e: Exception) {
            RepositoryResult.Failed(e.localizedMessage.orEmpty())
        }

    private suspend fun getListingCategories(): RepositoryResult<List<ListingCategory>> {
        return handleApiResult(
            apiResult = listingClient.getListingCategories(),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        ).first()
    }

    private suspend fun saveListingCategoriesToDatabase(listingCategories: List<ListingCategory>) {
        listingCategoryDao.replaceAll(
            categories = listingCategories.map { it.toDatabase() },
            subcategories = listingCategories.flatMap { categoryData ->
                categoryData.subcategories.map { subcategoryData ->
                    subcategoryData.toDatabase()
                }
            }
        )
    }

    private suspend fun getListingCategoriesAndSaveToDatabase(): List<ListingCategory>? {
        return when (val repositoryResult = getListingCategories()) {
            is RepositoryResult.Failed -> {
                appLogger.d(loggerTag) { "An error occurred whilst initializing the cached listing categories.\n" + repositoryResult.failureMessage }

                null
            }

            RepositoryResult.NetworkError -> {
                appLogger.d(loggerTag) { "A network error occurred whilst initializing the cached listing categories." }

                null
            }

            is RepositoryResult.Success -> {
                // Update database first.
                saveListingCategoriesToDatabase(repositoryResult.data)

                repositoryResult.data
            }
        }
    }
}