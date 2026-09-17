package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.common.extensions.toInstant
import com.alexgreece45.marvelx.core.common.extensions.toLocalDate
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.model.ListingStatus
import com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartInfo
import com.alexgreece45.marvelx.core.network.model.api.cart.ResponseCartSummary
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplace
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplaceCategorySection
import com.alexgreece45.marvelx.core.network.model.api.marketplace.ResponseMarketplacePromoSection
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.RequestUpdateListing
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListing
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingCategory
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingEnumGeneric
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingId
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingPreview
import com.alexgreece45.marvelx.core.network.model.api.marketplace.listing.ResponseListingSubcategory
import com.alexgreece45.marvelx.core.network.model.api.myListing.ResponseMyListing
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.model.marketplace.ListingSort
import com.alexgreece45.marvelx.core.network.services.ListingService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Single
internal class ListingClientImpl(
    private val listingService: ListingService
) : ListingClient {

    override suspend fun getListingCategories(): ApiResult<List<ResponseListingCategory>> {
        return ApiResult.Success(
            listOf(
                ResponseListingCategory(
                    id = 1,
                    categoryCode = "apparel",
                    label = "Apparel",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 1,
                            categoryId = 1,
                            categoryCode = "tshirt",
                            label = "T-shirts"
                        ),
                        ResponseListingSubcategory(
                            id = 2,
                            categoryId = 1,
                            categoryCode = "hoodie",
                            label = "Hoodies"
                        ),
                        ResponseListingSubcategory(
                            id = 3,
                            categoryId = 1,
                            categoryCode = "jacket",
                            label = "Jackets"
                        ),
                        ResponseListingSubcategory(
                            id = 4,
                            categoryId = 1,
                            categoryCode = "pajamas",
                            label = "Pajamas"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 2,
                    categoryCode = "drinkware",
                    label = "Drinkware",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 5,
                            categoryId = 2,
                            categoryCode = "mug",
                            label = "Mugs"
                        ),
                        ResponseListingSubcategory(
                            id = 6,
                            categoryId = 2,
                            categoryCode = "tumbler",
                            label = "Tumblers"
                        ),
                        ResponseListingSubcategory(
                            id = 7,
                            categoryId = 2,
                            categoryCode = "bottle",
                            label = "Water Bottles"
                        ),
                        ResponseListingSubcategory(
                            id = 8,
                            categoryId = 2,
                            categoryCode = "glass",
                            label = "Glasses"
                        ),
                        ResponseListingSubcategory(
                            id = 9,
                            categoryId = 2,
                            categoryCode = "shaker",
                            label = "Shakers"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 3,
                    categoryCode = "collectible",
                    label = "Collectibles",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 10,
                            categoryId = 3,
                            categoryCode = "figure",
                            label = "Action Figures"
                        ),
                        ResponseListingSubcategory(
                            id = 11,
                            categoryId = 3,
                            categoryCode = "funko",
                            label = "Funko Pop!"
                        ),
                        ResponseListingSubcategory(
                            id = 12,
                            categoryId = 3,
                            categoryCode = "statue",
                            label = "Statues"
                        ),
                        ResponseListingSubcategory(
                            id = 13,
                            categoryId = 3,
                            categoryCode = "modelkit",
                            label = "Model Kits"
                        ),
                        ResponseListingSubcategory(
                            id = 14,
                            categoryId = 3,
                            categoryCode = "bobble",
                            label = "Bobbleheads"
                        ),
                        ResponseListingSubcategory(
                            id = 15,
                            categoryId = 3,
                            categoryCode = "bust",
                            label = "Busts"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 4,
                    categoryCode = "art",
                    label = "Art & Posters",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 16,
                            categoryId = 4,
                            categoryCode = "poster",
                            label = "Posters"
                        ),
                        ResponseListingSubcategory(
                            id = 17,
                            categoryId = 4,
                            categoryCode = "canvas",
                            label = "Canvas Prints"
                        ),
                        ResponseListingSubcategory(
                            id = 18,
                            categoryId = 4,
                            categoryCode = "framed",
                            label = "Framed Art"
                        ),
                        ResponseListingSubcategory(
                            id = 19,
                            categoryId = 4,
                            categoryCode = "digital",
                            label = "Digital Prints"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 5,
                    categoryCode = "accessory",
                    label = "Accessories",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 20,
                            categoryId = 5,
                            categoryCode = "keychain",
                            label = "Keychains"
                        ),
                        ResponseListingSubcategory(
                            id = 21,
                            categoryId = 5,
                            categoryCode = "phonecase",
                            label = "Phone Cases"
                        ),
                        ResponseListingSubcategory(
                            id = 22,
                            categoryId = 5,
                            categoryCode = "wallet",
                            label = "Wallets"
                        ),
                        ResponseListingSubcategory(
                            id = 23,
                            categoryId = 5,
                            categoryCode = "backpack",
                            label = "Backpacks"
                        ),
                        ResponseListingSubcategory(
                            id = 24,
                            categoryId = 5,
                            categoryCode = "pin",
                            label = "Pins & Badges"
                        ),
                        ResponseListingSubcategory(
                            id = 25,
                            categoryId = 5,
                            categoryCode = "bracelet",
                            label = "Bracelets"
                        ),
                        ResponseListingSubcategory(
                            id = 26,
                            categoryId = 5,
                            categoryCode = "necklace",
                            label = "Necklaces"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 6,
                    categoryCode = "home",
                    label = "Home & Decor",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 27,
                            categoryId = 6,
                            categoryCode = "notebook",
                            label = "Notebooks"
                        ),
                        ResponseListingSubcategory(
                            id = 28,
                            categoryId = 6,
                            categoryCode = "sticker",
                            label = "Stickers"
                        ),
                        ResponseListingSubcategory(
                            id = 29,
                            categoryId = 6,
                            categoryCode = "pen",
                            label = "Pens"
                        ),
                        ResponseListingSubcategory(
                            id = 30,
                            categoryId = 6,
                            categoryCode = "mousepad",
                            label = "Mousepads"
                        ),
                        ResponseListingSubcategory(
                            id = 31,
                            categoryId = 6,
                            categoryCode = "calendar",
                            label = "Calendars"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 7,
                    categoryCode = "comics",
                    label = "Comics & Books",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 32,
                            categoryId = 7,
                            categoryCode = "issue",
                            label = "Single Issues"
                        ),
                        ResponseListingSubcategory(
                            id = 33,
                            categoryId = 7,
                            categoryCode = "graphic",
                            label = "Graphic Novels"
                        ),
                        ResponseListingSubcategory(
                            id = 34,
                            categoryId = 7,
                            categoryCode = "omnibus",
                            label = "Omnibuses"
                        ),
                        ResponseListingSubcategory(
                            id = 35,
                            categoryId = 7,
                            categoryCode = "collected",
                            label = "Collected Editions"
                        )
                    )
                ),
                ResponseListingCategory(
                    id = 8,
                    categoryCode = "cards",
                    label = "Trading Cards",
                    subcategories = listOf(
                        ResponseListingSubcategory(
                            id = 36,
                            categoryId = 8,
                            categoryCode = "base_card",
                            label = "Base Card"
                        ),
                        ResponseListingSubcategory(
                            id = 37,
                            categoryId = 8,
                            categoryCode = "foil_card",
                            label = "Foil Card"
                        ),
                        ResponseListingSubcategory(
                            id = 38,
                            categoryId = 8,
                            categoryCode = "holographic",
                            label = "Holographic"
                        ),
                        ResponseListingSubcategory(
                            id = 39,
                            categoryId = 8,
                            categoryCode = "full_art",
                            label = "Full Art"
                        ),
                        ResponseListingSubcategory(
                            id = 40,
                            categoryId = 8,
                            categoryCode = "sketch_card",
                            label = "Sketch Card"
                        ),
                        ResponseListingSubcategory(
                            id = 41,
                            categoryId = 8,
                            categoryCode = "serial_numbered",
                            label = "Serial Numbered"
                        ),
                        ResponseListingSubcategory(
                            id = 42,
                            categoryId = 8,
                            categoryCode = "signed_card",
                            label = "Signed Card"
                        ),
                        ResponseListingSubcategory(
                            id = 43,
                            categoryId = 8,
                            categoryCode = "promo_card",
                            label = "Promo Card"
                        )
                    )
                )
            )
        )
    }

    override suspend fun getListingStatuses(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric(
                    code = "draft",
                    label = "Draft",
                    description = "The listing is created but not publicly visible. The seller may still edit it."
                ),
                ResponseListingEnumGeneric(
                    code = "active",
                    label = "Active",
                    description = "The listing is visible and available for purchase. It appears in marketplace queries."
                ),
                ResponseListingEnumGeneric(
                    code = "reserved",
                    label = "Reserved",
                    description = "The listing is temporarily locked because a buyer has initiated checkout. Prevents other buyers from starting a purchase. If checkout fails or expires, the status may return to active."
                ),
                ResponseListingEnumGeneric(
                    code = "sold",
                    label = "Sold",
                    description = "The listing has been successfully purchased and is no longer available. It must remain visible for order history, reports, and receipts."
                ),
                ResponseListingEnumGeneric(
                    code = "cancelled",
                    label = "Cancelled",
                    description = "The seller has manually removed this listing from availability. It should not appear in the marketplace, but remains for audit and history."
                )
            )
        )
    }

    override suspend fun getListingConditions(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric("bad", "Bad", "1"),
                ResponseListingEnumGeneric("fair", "Fair", "2"),
                ResponseListingEnumGeneric("good", "Good", "3"),
                ResponseListingEnumGeneric("very_good", "Very Good", "4"),
                ResponseListingEnumGeneric("mint", "Mint", "5")
            )
        )
    }

    override suspend fun getListingApparelSizes(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric("XS", "Extra Small", "1"),
                ResponseListingEnumGeneric("S", "Small", "2"),
                ResponseListingEnumGeneric("M", "Medium", "3"),
                ResponseListingEnumGeneric("L", "Large", "4"),
                ResponseListingEnumGeneric("XL", "Extra Large", "5"),
                ResponseListingEnumGeneric("XXL", "2XL", "6"),
                ResponseListingEnumGeneric("XXXL", "3XL", "7"),
                ResponseListingEnumGeneric("XXXXL", "4XL", "8")
            )
        )
    }

    override suspend fun getListingMaterials(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric("ceramic", "Ceramic"),
                ResponseListingEnumGeneric("glass", "Glass"),
                ResponseListingEnumGeneric("metal", "Metal")
            )
        )
    }

    override suspend fun getListingCollectibleScales(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric("1_4", "1/4 Scale", "1"),
                ResponseListingEnumGeneric("1_6", "1/6 Scale", "2"),
                ResponseListingEnumGeneric("1_7", "1/7 Scale", "3"),
                ResponseListingEnumGeneric("1_10", "1/10 Scale", "4"),
                ResponseListingEnumGeneric("1_12", "1/12 Scale", "5")
            )
        )
    }

    override suspend fun getListingCollectibleBoxConditions(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric(
                    code = "destroyed",
                    label = "Destroyed",
                    sortOrder = "1",
                    description = "Box unusable with severe crushing, tears, or major structural damage."
                ),
                ResponseListingEnumGeneric(
                    code = "damaged",
                    label = "Damaged",
                    sortOrder = "2",
                    description = "Visible dents, tears, water damage, or significant wear."
                ),
                ResponseListingEnumGeneric(
                    code = "worn",
                    label = "Worn",
                    sortOrder = "3",
                    description = "Moderate wear such as edge scuffs, fading, or light creasing."
                ),
                ResponseListingEnumGeneric(
                    code = "good",
                    label = "Good",
                    sortOrder = "4",
                    description = "Minor wear but structurally intact and fully usable."
                ),
                ResponseListingEnumGeneric(
                    code = "excellent",
                    label = "Excellent",
                    sortOrder = "5",
                    description = "Very light signs of handling; overall excellent presentation."
                ),
                ResponseListingEnumGeneric(
                    code = "mint",
                    label = "Mint",
                    sortOrder = "6",
                    description = "Near-perfect condition with virtually no visible wear."
                ),
                ResponseListingEnumGeneric(
                    code = "sealed",
                    label = "Factory Sealed",
                    sortOrder = "7",
                    description = "Unopened and factory sealed, in original untouched condition."
                )
            )
        )
    }

    override suspend fun getListingGrades(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric("PR", "Poor", "1"),
                ResponseListingEnumGeneric("FR", "Fair", "2"),
                ResponseListingEnumGeneric("G", "Good", "3"),
                ResponseListingEnumGeneric("VG", "Very Good", "4"),
                ResponseListingEnumGeneric("FN", "Fine", "5"),
                ResponseListingEnumGeneric("VF", "Very Fine", "6"),
                ResponseListingEnumGeneric("NM", "Near Mint", "7")
            )
        )
    }

    override suspend fun getListingCardRarities(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric(
                    code = "common",
                    label = "Common",
                    sortOrder = "1"
                ),
                ResponseListingEnumGeneric(
                    code = "uncommon",
                    label = "Uncommon",
                    sortOrder = "2"
                ),
                ResponseListingEnumGeneric(
                    code = "rare",
                    label = "Rare",
                    sortOrder = "3"
                ),
                ResponseListingEnumGeneric(
                    code = "super_rare",
                    label = "Super Rare",
                    sortOrder = "4"
                ),
                ResponseListingEnumGeneric(
                    code = "ultra_rare",
                    label = "Ultra Rare",
                    sortOrder = "5"
                ),
                ResponseListingEnumGeneric(
                    code = "legendary",
                    label = "Legendary",
                    sortOrder = "6"
                ),
                ResponseListingEnumGeneric(
                    code = "grail",
                    label = "Grail",
                    sortOrder = "7"
                )
            )
        )
    }

    override suspend fun getListingCardVariants(): ApiResult<List<ResponseListingEnumGeneric>> {
        return ApiResult.Success(
            listOf(
                ResponseListingEnumGeneric(
                    code = "standard",
                    label = "Standard"
                ),
                ResponseListingEnumGeneric(
                    code = "foil",
                    label = "Foil"
                ),
                ResponseListingEnumGeneric(
                    code = "holo",
                    label = "Holographic"
                ),
                ResponseListingEnumGeneric(
                    code = "prism",
                    label = "Prism / Cracked Ice"
                ),
                ResponseListingEnumGeneric(
                    code = "full_art",
                    label = "Full Art"
                ),
                ResponseListingEnumGeneric(
                    code = "alternate_art",
                    label = "Alternate Art"
                ),
                ResponseListingEnumGeneric(
                    code = "sketch",
                    label = "Sketch Card"
                ),
                ResponseListingEnumGeneric(
                    code = "signed",
                    label = "Signed"
                ),
                ResponseListingEnumGeneric(
                    code = "serial_numbered",
                    label = "Serial Numbered"
                )
            )
        )
    }

    override suspend fun getMarketplaceInfo(): ApiResult<ResponseMarketplace> {
        val prices = listOf("20.25", "9999", "56.98", "15", "7.89", "100.5", "8.56", "6.99", "8.2")
        return ApiResult.Success(
            ResponseMarketplace(
                promo = ResponseMarketplacePromoSection(
                    title = "Top Christmas Choices!",
                    listings = prices.mapIndexed { index, price ->
                        val listingId = "${index + 1}"
                        ResponseListingPreview(
                            listingId = listingId,
                            title = "Listing #$listingId",
                            price = price,
                            coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/$listingId/profile-image",
                            favorite = Random.nextBoolean(),
                            cart = Random.nextBoolean()
                        )
                    }
                ),
                categories = listOf(
                    ResponseMarketplaceCategorySection(
                        title = "Apparel",
                        categoryCode = "apparel",
                        listings = listOf(
                            ResponseListingPreview(
                                listingId = "1",
                                title = "Listing #1",
                                price = "56.98",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/3/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            )
                        )
                    ),
                    ResponseMarketplaceCategorySection(
                        title = "Drinkware",
                        categoryCode = "drinkware",
                        listings = listOf(
                            ResponseListingPreview(
                                listingId = "1",
                                title = "Listing #1",
                                price = "12.2589",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/2/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            ),
                            ResponseListingPreview(
                                listingId = "3",
                                title = "Listing #3",
                                price = "56.9812",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/3/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            ),
                            ResponseListingPreview(
                                listingId = "4",
                                title = "Listing #1",
                                price = "20.25",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            ),
                            ResponseListingPreview(
                                listingId = "5",
                                title = "Listing #2",
                                price = "23223",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/2/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            ),
                            ResponseListingPreview(
                                listingId = "6",
                                title = "Listing #3",
                                price = "56.98",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/3/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            ),
                            ResponseListingPreview(
                                listingId = "345",
                                title = "Listing #1",
                                price = "20.25",
                                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/1/profile-image",
                                favorite = Random.nextBoolean(),
                                cart = Random.nextBoolean()
                            ),
                        )
                    ),
                )
            )
        )
    }

    override suspend fun getAllListingsByCategory(
        categoryCode: String
    ): ApiResult<List<ResponseListing>> {
        return executeApiServiceCall { listingService.getAllListingsByCategory(categoryCode) }
    }

    override suspend fun getCurrentUserListings(page: Int): ApiResult<List<ResponseMyListing>> {
        delay(2.5.seconds)

        val startIndex = 1 + (page - 1) * 10
        return ApiResult.Success(
            (startIndex..page * 10).map { listingId ->
                val stringifiedListingId = listingId.toString()
                val year = "20${if (stringifiedListingId.length == 1) 0 else ""}$listingId"
                ResponseMyListing(
                    listingId = stringifiedListingId,
                    title = "Rare $year Spider-Man Limited Edition Mug",
                    coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/${if (listingId % 2 == 0) 1 else 2}/profile-image",
                    statusCode = ListingStatus.entries.shuffled().first().code
                )
            }
        )
    }

    override suspend fun getUserListings(
        userId: String,
        page: Int
    ): ApiResult<List<ResponseListingPreview>> {
        delay(2.5.seconds)

        val startIndex = 1 + (page - 1) * 10
        return ApiResult.Success(
            (startIndex..page * 10).map { listingId ->
                val stringifiedListingId = listingId.toString()
                val year = "20${if (stringifiedListingId.length == 1) 0 else ""}$listingId"
                ResponseListingPreview(
                    listingId = stringifiedListingId,
                    title = "Rare $year Spider-Man Limited Edition Mug",
                    price = Random.nextDouble(from = 5.0, until = 200.0).toString(),
                    coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/${if (listingId % 2 == 0) 1 else 2}/profile-image",
                    favorite = Random.nextBoolean(),
                    cart = Random.nextBoolean()
                )
            }
        )
    }

    override suspend fun getListing(listingId: String): ApiResult<ResponseListing> {
        val year = "20${if (listingId.length == 1) 0 else ""}$listingId"

        return ApiResult.Success(
            ResponseListing(
                listingId = listingId,
                sellerUserId = if ((listingId.toIntOrNull() ?: 0) % 2 == 0) "1" else Random.nextInt(
                    2..10
                ).toString(),
                categoryCode = "drinkware",
                subcategoryCode = "mug",
                conditionCode = "mint",
                statusCode = "active",
                title = "Rare $year Spider-Man Limited Edition Mug",
                description = "Exclusive $year edition Spider-Man mug. It is of high quality.",
                price = Random.nextDouble(from = 5.0, until = 200.0).toString(),
                coverImageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/${if (listingId.toInt() % 2 == 0) 1 else 2}/profile-image",
                imageUrls = listOf(
                    "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/2/profile-image",
                    "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/4/profile-image",
                ),
                details = JsonObject().apply {
                    addProperty("materialCode", "ceramic")
                    addProperty("capacityMl", "300")
                    addProperty("dishwasherSafe", true)
                },
                finalSale = Random.nextBoolean(),
                postedAt = DateUtil.getUTCInstant().toString(),
                updatedAt = null,
                favorite = Random.nextBoolean(),
                cart = Random.nextBoolean()
            )
        )
    }

    override suspend fun getFilteredListings(
        searchQuery: String,
        categoryIds: List<String>,
        conditionIds: List<String>,
        minPrice: String?,
        maxPrice: String?,
        datePostedKey: String,
        customDateFrom: String?,
        customDateTo: String?,
        finalSale: Boolean,
        sortKey: String
    ): ApiResult<List<ResponseListing>> {
        val mockDatabaseListings = listOf(
            // --- The Similar Items (For price range & text search testing) ---
            ResponseListing(
                listingId = "1",
                sellerUserId = "2",
                categoryCode = "comics",
                subcategoryCode = "collected",
                conditionCode = "good",
                statusCode = "active",
                title = "Amazing Spider-Man Vintage Comic #12",
                description = "",
                price = "15.50",
                coverImageUrl = getImageUrl("1"),
                imageUrls = listOf(getImageUrl("1")),
                details = JsonObject().apply {
                    addProperty("gradeCode", "FN")
                    addProperty("publisher", "Marvel")
                    addProperty("year", "1980")
                },
                finalSale = false,
                postedAt = LocalDate.of(2026, 8, 30).toInstant().toString(),
                updatedAt = null,
                favorite = false,
                cart = true,
                orderIds = null
            ),
            ResponseListing(
                listingId = "2",
                sellerUserId = "3",
                categoryCode = "comics",
                subcategoryCode = "issue",
                conditionCode = "very_good",
                statusCode = "active",
                title = "Amazing Spider-Man Classic Comic #15",
                description = "",
                price = "15.99",
                coverImageUrl = getImageUrl("2"),
                imageUrls = listOf(getImageUrl("2")),
                details = JsonObject().apply {
                    addProperty("gradeCode", "VF")
                    addProperty("publisher", "Marvel")
                    addProperty("year", "2001")
                },
                finalSale = false,
                postedAt = LocalDate.of(2026, 8, 28).toInstant().toString(),
                updatedAt = null,
                favorite = true,
                cart = false,
                orderIds = null
            ),

            // --- The Random Items ---
            ResponseListing(
                listingId = "3",
                sellerUserId = "2",
                categoryCode = "drinkware",
                subcategoryCode = "mug",
                conditionCode = "mint",
                statusCode = "active",
                title = "Rare 2001 Spider-Man Limited Edition Mug",
                description = "",
                price = "45.00",
                coverImageUrl = getImageUrl("2"),
                imageUrls = listOf(getImageUrl("2")),
                finalSale = false,
                details = JsonObject().apply {
                    addProperty("materialCode", "ceramic")
                    addProperty("capacityMl", "400")
                    addProperty("dishwasherSafe", true)
                },
                postedAt = LocalDate.of(2026, 8, 25).toInstant().toString(),
                updatedAt = null,
                favorite = true,
                cart = false,
                orderIds = null
            ),
            ResponseListing(
                listingId = "4",
                sellerUserId = "4",
                categoryCode = "art",
                subcategoryCode = "canvas",
                conditionCode = "mint",
                statusCode = "active",
                title = "Original Iron Man Mark V Blueprint Canvas - 24x36",
                description = "",
                price = "120.00",
                coverImageUrl = getImageUrl("1"),
                imageUrls = listOf(getImageUrl("1")),
                details = JsonObject().apply {
                    addProperty("width", "24")
                    addProperty("height", "36")
                    addProperty("framed", true)
                },
                finalSale = true,
                postedAt = LocalDate.of(2026, 6, 12).toInstant().toString(),
                updatedAt = null,
                favorite = false,
                cart = Random.nextBoolean(),
                orderIds = null
            ),
            ResponseListing(
                listingId = "5",
                sellerUserId = "6",
                categoryCode = "apparel",
                subcategoryCode = "jacket",
                conditionCode = "fair",
                statusCode = "active",
                title = "Wolverine Logan Distressed Leather Jacket - Men's L",
                description = "",
                price = "85.00",
                coverImageUrl = getImageUrl("4"),
                imageUrls = listOf(getImageUrl("4")),
                details = JsonObject().apply {
                    addProperty("sizeCode", "L")
                    addProperty("color", "brown") // TODO: Revisit.
                },
                finalSale = false,
                postedAt = LocalDate.of(2026, 8, 1).toInstant().toString(),
                updatedAt = null,
                favorite = false,
                cart = Random.nextBoolean(),
                orderIds = null
            )
        )

        val filteredListings = mockDatabaseListings.filter { item ->

            // 1. Search Query
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true)

            // 2. Multi-Select Chips
            val matchesCategory = categoryIds.isEmpty() || item.categoryCode in categoryIds
            val matchesCondition = conditionIds.isEmpty() || item.conditionCode in conditionIds

            // 3. Price Range
            val minPriceBigDecimal = minPrice?.toBigDecimalOrNull()
            val maxPriceBigDecimal = maxPrice?.toBigDecimalOrNull()

            val itemPrice = item.price.toBigDecimal()

            val matchesPrices = when {
                minPriceBigDecimal != null && maxPriceBigDecimal != null -> itemPrice in minPriceBigDecimal..maxPriceBigDecimal
                minPriceBigDecimal != null -> itemPrice >= minPriceBigDecimal
                maxPriceBigDecimal != null -> itemPrice <= maxPriceBigDecimal
                else -> true
            }

            // 4. Date Posted
            val itemPostedAt = Instant.parse(item.postedAt).toLocalDate()

            val today = DateUtil.getCurrentLocalDate()
            val matchesDate = when (datePostedKey.uppercase()) {
                "TODAY" -> itemPostedAt.isEqual(today)
                "LAST_7_DAYS" -> !itemPostedAt.isBefore(today.minusDays(7))
                "CUSTOM" -> {
                    val from = customDateFrom?.let {
                        runCatching { LocalDate.parse(it) }.getOrNull()
                    }
                    val to = customDateTo?.let {
                        runCatching { LocalDate.parse(it) }.getOrNull()
                    }

                    val afterFrom = from == null || !itemPostedAt.isBefore(from)
                    val beforeTo = to == null || !itemPostedAt.isAfter(to)

                    afterFrom && beforeTo
                }

                else -> true // "ALL" or unrecognized key
            }

            // 5. Final Sale Toggle
            // Assumes that if the user doesn't check the toggle, they want to see all items.
            // If checked, they ONLY want to see final sale items.
            val matchesFinalSale = if (finalSale) item.finalSale else true

            matchesSearch
                    && matchesCategory
                    && matchesCondition
                    && matchesPrices
                    && matchesDate
                    && matchesFinalSale

        }.let { filteredList ->
            val sortOption = ListingSort.entries.firstOrNull { it.key == sortKey }

            when (sortOption) {
                ListingSort.PRICE_ASCENDING -> filteredList.sortedBy { it.price }
                ListingSort.PRICE_DESCENDING -> filteredList.sortedByDescending { it.price }
                ListingSort.DATE_POSTED_ASCENDING -> filteredList.sortedBy {
                    Instant.parse(it.postedAt).toLocalDate()
                }

                ListingSort.DATE_POSTED_DESCENDING -> filteredList.sortedByDescending {
                    Instant.parse(
                        it.postedAt
                    ).toLocalDate()
                }

                null -> filteredList // Fallback to the original order if the key is missing or invalid
            }
        }

        return ApiResult.Success(filteredListings)
    }

    override suspend fun getFilteredListingsPaged(
        searchQuery: String?,
        categoryCodes: List<String>?,
        conditionCodes: List<String>?,
        minPrice: String?,
        maxPrice: String?,
        datePostedKey: String,
        customDateFrom: String?,
        customDateTo: String?,
        finalSale: Boolean,
        sortKey: String,
        offset: Int,
        limit: Int
    ): ApiResult<List<ResponseListing>> {
        // Reuse the full mock filter/sort pipeline, then slice it the way a real offset/limit
        // endpoint would. This lets the mock variant exercise real paging behaviour end to end,
        // including the "last page returns fewer rows than asked for" end-of-pagination signal.
        val allMatches = getFilteredListings(
            searchQuery.orEmpty(),
            categoryCodes.orEmpty(),
            conditionCodes.orEmpty(),
            minPrice,
            maxPrice,
            datePostedKey,
            customDateFrom,
            customDateTo,
            finalSale,
            sortKey
        )

        return when (allMatches) {
            is ApiResult.Success -> ApiResult.Success(
                allMatches.body.drop(offset).take(limit)
            )

            else -> allMatches
        }
    }

    override suspend fun createListing(body: MultipartBody): ApiResult<ResponseListingId> =
        ApiResult.Success(ResponseListingId("100"))

    override suspend fun updateListing(
        listingId: String,
        request: RequestUpdateListing
    ): ApiResult<String> = ApiResult.Success("")

    override suspend fun deleteListing(listingId: String): ApiResult<String> {
        delay(1500L.milliseconds)
        return ApiResult.Success("")
    }

    override suspend fun addToFavorites(listingId: String): ApiResult<String> {
        delay(1.5.seconds)
        return ApiResult.Success("")
    }

    override suspend fun removeFromFavorites(listingId: String): ApiResult<String> {
        delay(1.5.seconds)
        return ApiResult.Success("")
    }


    override suspend fun getFavoriteListings(): ApiResult<List<ResponseListingPreview>> {
        return executeApiServiceCall {
            listingService.getFavoriteListings()
        }
    }

    override suspend fun getCartInfo(): ApiResult<ResponseCartInfo> {
        val items = (getUserListings(userId = "1", page = 1) as ApiResult.Success).body.take(5)
        val subtotalPrice = items.sumOf { it.price.toDouble() }
        val shippingPrice = Random.nextDouble(2.5, 6.0)
        val totalPrice = subtotalPrice + shippingPrice

        return ApiResult.Success(
            ResponseCartInfo(
                items = items,
                summary = ResponseCartSummary(
                    subtotalPrice = subtotalPrice.toString(),
                    shippingPrice = shippingPrice.toString(),
                    totalPrice = totalPrice.toString()
                )
            )
        )
    }

    override suspend fun getCartItems(): ApiResult<List<ResponseListingPreview>> {
        return executeApiServiceCall {
            listingService.getCartItems()
        }
    }

    override suspend fun getCartSummary(): ApiResult<ResponseCartSummary> {
        return executeApiServiceCall {
            listingService.getCartSummary()
        }
    }

    override suspend fun addCartItem(listingId: String): ApiResult<String> {
        delay(1.5.seconds)
        return ApiResult.Success("")
    }

    override suspend fun deleteCartItem(listingId: String): ApiResult<String> {
        delay(1.5.seconds)
        return ApiResult.Success("")
    }

    // Helper functions.

    private fun getImageUrl(userId: String): String =
        "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/$userId/profile-image"
}