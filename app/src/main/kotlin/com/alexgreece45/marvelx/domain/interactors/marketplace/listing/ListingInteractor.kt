package com.alexgreece45.marvelx.domain.interactors.marketplace.listing

import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoTransformationNoFlow
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.ListingDomainMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.toRequest
import com.alexgreece45.marvelx.domain.mappers.marketplace.toDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.CreateListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.ListingPreviewDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.UpdateListingDomain
import com.alexgreece45.marvelx.domain.model_api.marketplace.listing.toDomain
import com.alexgreece45.marvelx.domain.model_api.myListing.MyListingDomain
import com.alexgreece45.marvelx.domain.model_api.myListing.toDomain
import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import com.alexgreece45.marvelx.util.extensions.getFileNameExtension
import com.alexgreece45.marvelx.util.extensions.getMediaTypeFromFileName
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

interface ListingInteractor {

    suspend fun getAllListingsByCategory(categoryCode: String): InteractorPartialState<List<ListingDomain>>

    suspend fun getCurrentUserListings(page: Int): InteractorPartialState<List<MyListingDomain>>

    suspend fun getUserListings(
        userId: String,
        page: Int
    ): InteractorPartialState<List<ListingPreviewDomain>>

    suspend fun getListing(listingId: String): InteractorPartialState<ListingDomain>
    suspend fun createListing(createListingDomain: CreateListingDomain): InteractorPartialState<String>
    suspend fun updateListing(
        listingId: String,
        updateListingDomain: UpdateListingDomain
    ): Boolean

    suspend fun deleteListing(listingId: String): Boolean

    suspend fun addToFavorites(listingId: String): Boolean
    suspend fun removeFromFavorites(listingId: String): Boolean

    suspend fun getFavoriteListings(): InteractorPartialState<List<ListingPreviewDomain>>
}

class ListingInteractorImpl(
    private val listingRepository: ListingRepository,
    private val listingDomainMapper: ListingDomainMapper
) : ListingInteractor {

    override suspend fun getAllListingsByCategory(categoryCode: String): InteractorPartialState<List<ListingDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getAllListingsByCategory(categoryCode),
            transformation = { this.map { listingDomainMapper.toDomain(it) } }
        )
    }

    override suspend fun getCurrentUserListings(page: Int): InteractorPartialState<List<MyListingDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getCurrentUserListings(page),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): InteractorPartialState<List<ListingPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getUserListings(userId, page),
            transformation = { this.map { it.toDomain() } }
        )
    }

    override suspend fun getListing(listingId: String): InteractorPartialState<ListingDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getListing(listingId),
            transformation = listingDomainMapper::toDomain
        )
    }

    override suspend fun createListing(createListingDomain: CreateListingDomain): InteractorPartialState<String> {
        val coverImagePart = createImageBodyPart(createListingDomain.coverImage, "cover", 1)
        val imageParts = createListingDomain.allImages.mapIndexed { index, image ->
            createImageBodyPart(image, "images", index + 1)
        }

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addPart(coverImagePart)
        imageParts.forEach { imagePart ->
            builder.addPart(imagePart)
        }
        builder.addFormDataPart("title", createListingDomain.title)
        builder.addFormDataPart("description", createListingDomain.description)
        builder.addFormDataPart("category_id", createListingDomain.categoryId.toString())
        builder.addFormDataPart("subcategory_id", createListingDomain.subcategoryId.toString())
        builder.addFormDataPart("condition_code", createListingDomain.conditionCode)
        builder.addFormDataPart("price", createListingDomain.price.toPlainString())

        return handleRepositoryResultNoTransformationNoFlow(
            listingRepository.createListing(
                builder.build()
            )
        )
    }

    override suspend fun updateListing(
        listingId: String,
        updateListingDomain: UpdateListingDomain
    ): Boolean {
        return listingRepository.updateListing(listingId, updateListingDomain.toRequest())
    }

    override suspend fun deleteListing(listingId: String): Boolean {
        return listingRepository.deleteListing(listingId)
    }

    override suspend fun addToFavorites(listingId: String): Boolean {
        return listingRepository.addToFavorites(listingId)
    }

    override suspend fun removeFromFavorites(listingId: String): Boolean {
        return listingRepository.removeFromFavorites(listingId)
    }

    override suspend fun getFavoriteListings(): InteractorPartialState<List<ListingPreviewDomain>> {
        return handleRepositoryResultNoFlow(
            repositoryResult = listingRepository.getFavoriteListings(),
            transformation = { this.map { it.toDomain() } }
        )
    }

    private fun createImageBodyPart(
        readFileContentData: ReadFileContentData,
        fieldName: String,
        imageIndex: Int
    ): MultipartBody.Part {
        val imageRequestBody = readFileContentData.fileContent.toRequestBody(
            readFileContentData.fileName.getMediaTypeFromFileName()
        )

        return MultipartBody.Part.createFormData(
            name = fieldName,
            filename = "listing_image_$imageIndex." + readFileContentData.fileName.getFileNameExtension(),
            body = imageRequestBody
        )
    }
}
