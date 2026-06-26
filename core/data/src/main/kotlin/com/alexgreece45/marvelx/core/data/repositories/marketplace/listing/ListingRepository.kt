package com.alexgreece45.marvelx.core.data.repositories.marketplace.listing

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.MultipartBuilder
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartBuilderResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartItem
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.mappers.ListingDataMapper
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.CreateListingData
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingData
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.ListingPreviewData
import com.alexgreece45.marvelx.core.data.model.marketplace.listing.toData
import com.alexgreece45.marvelx.core.data.model.myListing.MyListingData
import com.alexgreece45.marvelx.core.data.model.myListing.toData
import com.alexgreece45.marvelx.core.network.clients.ListingClient
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface ListingRepository {

    suspend fun getAllListingsByCategory(categoryCode: String): RepositoryResult<List<ListingData>>

    suspend fun getCurrentUserListings(page: Int): RepositoryResult<List<MyListingData>>

    suspend fun getUserListings(
        userId: String,
        page: Int
    ): RepositoryResult<List<ListingPreviewData>>

    suspend fun getListing(listingId: String): RepositoryResult<ListingData>
    suspend fun createListing(createListingData: CreateListingData): MultipartResult<String>

    suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): Boolean

    suspend fun deleteListing(listingId: String): Boolean

    suspend fun addToFavorites(listingId: String): Boolean
    suspend fun removeFromFavorites(listingId: String): Boolean

    suspend fun getFavoriteListings(): RepositoryResult<List<ResponseListingPreview>>
}

@Single
internal class ListingRepositoryImpl(
    private val listingClient: ListingClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val listingDataMapper: ListingDataMapper,
    private val multipartBuilder: MultipartBuilder
) : ListingRepository {

    override suspend fun getAllListingsByCategory(
        categoryCode: String
    ): RepositoryResult<List<ListingData>> {
        return handleApiResultNoFlow(
            apiResult = listingClient.getAllListingsByCategory(categoryCode),
            transformation = { this.map { listingDataMapper.toData(it) } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getCurrentUserListings(page: Int): RepositoryResult<List<MyListingData>> {
        return handleApiResultNoFlow(
            apiResult = listingClient.getCurrentUserListings(page),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): RepositoryResult<List<ListingPreviewData>> {
        return handleApiResultNoFlow(
            apiResult = listingClient.getUserListings(userId, page),
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getListing(listingId: String): RepositoryResult<ListingData> {
        return handleApiResultNoFlow(
            apiResult = listingClient.getListing(listingId),
            transformation = listingDataMapper::toData,
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun createListing(createListingData: CreateListingData): MultipartResult<String> {
        val multipartImageItems = buildList {
            add(
                MultipartItem.Image(
                    partName = "cover",
                    fileName = "listing_image_1", // TODO: May be changed to "listing_image_cover" in the future
                    imageFileData = createListingData.coverImage
                )
            )
            createListingData.additionalImages.forEachIndexed { index, attachedImageData ->
                add(
                    MultipartItem.Image(
                        partName = "images",
                        fileName = "listing_image_${index + 1}",
                        imageFileData = attachedImageData
                    )
                )
            }
        }

        val multipartBuilderResult = multipartBuilder.constructMultipartBody(
            items = buildList {
                addAll(multipartImageItems)
                add(MultipartItem.Text("title", createListingData.title))
                add(MultipartItem.Text("description", createListingData.description))
                add(MultipartItem.Text("category_id", createListingData.categoryCode))
                add(MultipartItem.Text("subcategory_id", createListingData.subcategoryCode))
                add(MultipartItem.Text("condition_code", createListingData.conditionCode))
                add(MultipartItem.Text("price", createListingData.price.toPlainString()))
            }
        )

        return when (multipartBuilderResult) {
            is MultipartBuilderResult.Success -> {
                when (
                    val result = handleApiResultNoFlow(
                        apiResult = listingClient.createListing(body = multipartBuilderResult.data),
                        transformation = ResponseListingId::listingId,
                        ioDispatcher = ioDispatcher
                    )
                ) {
                    is RepositoryResult.Failed -> MultipartResult.GenericError(
                        result.failureMessage
                    )

                    RepositoryResult.NetworkError -> MultipartResult.NetworkError

                    is RepositoryResult.Success -> MultipartResult.Success(result.data)
                }
            }

            is MultipartBuilderResult.GenericError -> MultipartResult.GenericError(
                multipartBuilderResult.errorMessage
            )

            is MultipartBuilderResult.ImageProcessingError -> MultipartResult.ImageProcessingError
        }
    }

    override suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.updateListing(listingId, request),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun deleteListing(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.deleteListing(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun addToFavorites(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.addToFavorites(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun removeFromFavorites(listingId: String): Boolean {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.removeFromFavorites(listingId),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }

    override suspend fun getFavoriteListings(): RepositoryResult<List<ResponseListingPreview>> {
        return handleApiResultNoTransformationWithData(
            apiResult = listingClient.getFavoriteListings(),
            ioDispatcher = ioDispatcher
        ).first()
    }
}