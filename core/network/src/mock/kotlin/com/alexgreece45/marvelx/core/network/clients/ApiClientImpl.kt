package com.alexgreece45.marvelx.core.network.clients

import android.content.Context
import com.alexgreece45.marvelx.core.network.constants.TokenConstants
import com.alexgreece45.marvelx.core.network.model.api.authorize.ResponseAuthorize
import com.alexgreece45.marvelx.core.network.model.api.character.CharacterDataContainer
import com.alexgreece45.marvelx.core.network.model.api.character.CharacterDataWrapper
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ComicDataContainer
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.core.network.model.api.device.RequestRegisterDevice
import com.alexgreece45.marvelx.core.network.model.api.device.RequestUpdateDevice
import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaqs
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.RequestForgotPasswordSendOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.RequestForgotPasswordValidateOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.ResponseForgotPasswordSendOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.ResponseForgotPasswordValidateOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseReturnPolicies
import com.alexgreece45.marvelx.core.network.model.api.otp.OtpRequest
import com.alexgreece45.marvelx.core.network.model.api.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.ResponseResendOtpSuccess
import com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.core.network.model.api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.core.network.model.api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.core.network.model.api.user.login.ResponseLogin
import com.alexgreece45.marvelx.core.network.model.api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.core.network.model.api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.core.network.model.api.user.postLogin.Sign
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseUserStats
import com.alexgreece45.marvelx.core.network.model.api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.core.network.model.api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.ApiService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.koin.core.annotation.Single
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.time.Duration.Companion.seconds

@Single
internal class ApiClientImpl(
    private val context: Context,
    private val gson: Gson,
    private val apiService: ApiService,
) : ApiClient {

    private val currentUserId = "1"

    private val basicProfileDetails: ResponseBasicProfileDetails = gson.fromJson(
        readFileContents("mock_profile_details.json"),
        ResponseBasicProfileDetails::class.java
    )

    private val verificationStep = "verification_pin_email"
    private val forgotPasswordRequestId = "1001"

    private fun getProfileDetails(targetUserId: String): ResponseProfileDetails {
        return if (targetUserId == currentUserId) {
            ResponseProfileDetails(
                basicProfileDetails = basicProfileDetails,
                isDifferentUser = false,
                relationshipStatus = null,
                privacySettings = null,
                userStats = ResponseUserStats(
                    friendsCount = 2,
                    listingsCount = 5,
                    favoritesCount = 8
                )
            )
        } else {
            ResponseProfileDetails(
                basicProfileDetails = gson.fromJson(
                    readFileContents("mock_profile_details_2.json"),
                    ResponseBasicProfileDetails::class.java
                ),
                isDifferentUser = false,
                relationshipStatus = null,
                privacySettings = getPrivacySettings(targetUserId),
                userStats = ResponseUserStats(
                    friendsCount = 10,
                    listingsCount = 0,
                    favoritesCount = 5
                )
            )
        }
    }

    private fun getPrivacySettings(targetUserId: String): ResponsePrivacySettings {
        return if (targetUserId == currentUserId) {
            ResponsePrivacySettings(
                isFriendsListVisible = true,
                isListingsListVisible = true,
                isFavoritesListVisible = false,
                isGenderVisible = true,
                isEmailVisible = false
            )
        } else {
            ResponsePrivacySettings(
                isFriendsListVisible = false,
                isListingsListVisible = true,
                isFavoritesListVisible = false,
                isGenderVisible = false,
                isEmailVisible = true
            )
        }
    }

    override suspend fun getCharacters(): Response<CharacterDataWrapper> {
        return Response.success(
            200,
            gson.fromJson(
                readFileContents("mock_get_characters.json"),
                CharacterDataWrapper::class.java
            )
        )
    }

    override suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper> {
        return try {
            val response = gson.fromJson(
                readFileContents("mock_get_characters.json"),
                CharacterDataWrapper::class.java
            )
            val character = response.data.results.find { it.id == characterId }
            if (character == null) {
                Response.error<String>(
                    404,
                    "Character with id \"$characterId\" was not found in mock_get_characters.json.".toResponseBody()
                )
            }

            Response.success(
                200,
                CharacterDataWrapper(
                    code = 0,
                    status = "",
                    copyright = "",
                    attributionText = "",
                    attributionHTML = "",
                    data = CharacterDataContainer(
                        offset = 0,
                        limit = 0,
                        total = 0,
                        count = 0,
                        results = listOf(character!!)
                    ),
                    etag = ""
                )
            )
        } catch (_: Exception) {
            Response.error(
                500,
                "An error occurred while parsing \"mock_get_characters.json\".".toResponseBody()
            )
        }
    }

    /**
     * Gets the comics for a specific characterId.
     *
     * ### Note: Since this is the mock version, the comics for the character "A-Bomb (HAS)" are always returned.
     *
     * @param characterId the character's id
     */
    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> =
        try {
            val response = gson.fromJson(
                readFileContents("mock_get_character_comics.json"),
                ComicDataWrapper::class.java
            )

            Response.success(
                200,
                ComicDataWrapper(
                    code = 0,
                    status = "",
                    copyright = "",
                    attributionText = "",
                    attributionHTML = "",
                    data = ComicDataContainer(
                        offset = 0,
                        limit = 0,
                        total = 0,
                        count = 0,
                        results = response.data.results
                    ),
                    etag = ""

                )
            )
        } catch (_: Exception) {
            Response.error(
                500,
                "An error occurred while parsing \"mock_get_character_comics.json\".".toResponseBody()
            )
        }

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> =
        // Arbitrary characterId value, since no search operation is executed.
        getCharacterComics(0).let { comics ->
            return when (comics.isSuccessful) {
                false -> {
                    Response.error(404, comics.errorBody() ?: "".toResponseBody())
                }

                true -> {
                    val comic = comics.body()?.data?.results?.find { it.id == comicId }
                    if (comic == null) Response.error<String>(
                        404,
                        "Comic with id \"$comicId\" was not found in mock_get_character_comics.json.".toResponseBody()
                    )

                    Response.success(
                        200,
                        ComicDataWrapper(
                            code = 0,
                            status = "",
                            copyright = "",
                            attributionText = "",
                            attributionHTML = "",
                            data = ComicDataContainer(
                                offset = 0,
                                limit = 0,
                                total = 0,
                                count = 0,
                                results = listOf(comic!!)
                            ),
                            etag = ""
                        )
                    )
                }
            }
        }

    override suspend fun signup(requestUserSignup: RequestUserSignup): Response<ResponseSignup> =
        Response.success(
            200,
            ResponseSignup(
                verificationStep = verificationStep,
                userId = currentUserId,
                tempAuthCode = "tempAuthCode"
            )
        )

    override suspend fun login(requestUserLogin: RequestUserLogin): Response<ResponseLogin> =
        Response.success(
            200,
            ResponseLogin(
                verificationStep = verificationStep,
                userId = currentUserId
            )
        )


    override suspend fun loginWithToken(): Response<ResponseLoginWithToken> =
        Response.success(
            200,
            ResponseLoginWithToken(
                verificationStep = verificationStep
            )
        )

    override suspend fun postLogin(accessToken: String): Response<ResponsePostLogin> =
        Response.success(
            200,
            ResponsePostLogin(
                sign = Sign(
//                    key = readRawTextFile(R.raw.backend_certificate_https_server), // TODO: Review if required.
                    key = "readRawTextFile(R.raw.backend_certificate_https_server)",
                    algorithm = "HmacSHA256"
                )
            )
        )

    override suspend fun authorize(
        tempAuthCode: String,
        clientId: String,
        codeChallenge: String,
        codeChallengeMethod: String
    ): Response<ResponseAuthorize> =
        Response.success(
            200,
            ResponseAuthorize(
                authCode = "AUTH_CODE"
            )
        )

    override suspend fun grandNewAccessToken(
        clientId: String,
        codeVerifier: String,
        code: String,
        uri: String
    ): Response<ResponseToken> = Response.success(
        200,
        TokenConstants.responseToken
    )

    override suspend fun registerDevice(requestRegisterDevice: RequestRegisterDevice): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun updateDevice(requestUpdateDevice: RequestUpdateDevice): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun logout(clientId: String, refreshToken: String): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess> =
        Response.success(
            200,
            OtpResponseSuccess(tempAuthCode = "")
        )

    override suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess> =
        Response.success(
            200,
            ResponseResendOtpSuccess(expiresIn = 5L)
        )

    override suspend fun forgotPasswordSendOtp(body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp> =
        Response.success(
            200,
            ResponseForgotPasswordSendOtp(requestId = forgotPasswordRequestId)
        )

    override suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail> =
        apiService.forgotPasswordIsOtpBlockedEmail(body)

    override suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber> =
        apiService.forgotPasswordIsOtpBlockedPhoneNumber(body)

    override suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp> =
        Response.success(
            200,
            ResponseForgotPasswordValidateOtp(isPhoneNumberRequired = false)
        )

    override suspend fun getBasicProfileDetails(userId: String): ApiResult<ResponseBasicProfileDetails> =
        ApiResult.Success(body = basicProfileDetails)

    override suspend fun getCurrentUserProfileDetails(): Response<ResponseBasicProfileDetails> =
        Response.success(basicProfileDetails)

    override suspend fun updateCurrentUserProfileDetails(requestProfileDetailsUpdate: RequestProfileDetailsUpdate): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun updateUserProfileImage(
        userId: String,
        profileImage: MultipartBody.Part
    ): ApiResult<String> = ApiResult.Success("")

    override suspend fun getUserProfileDetails(userId: String): Response<ResponseProfileDetails> =
        Response.success(
            200,
            getProfileDetails(targetUserId = userId)
        )

    override suspend fun getUserPrivacySettings(): ApiResult<ResponsePrivacySettings> =
        ApiResult.Success(getPrivacySettings(currentUserId))

    override suspend fun updateUserPrivacySettings(requestPrivacySettings: RequestPrivacySettings): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun getUserFriends(userId: String): ApiResult<List<ResponseFriend>> =
        ApiResult.Success(emptyList())

    override suspend fun getIncomingFriendRequests(): ApiResult<List<ResponseIncomingFriendRequest>> =
        ApiResult.Success(emptyList())

    override suspend fun sendFriendRequest(userId: String): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun acceptFriendRequest(userId: String): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun deleteFriendRequest(userId: String, type: String): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun getFriends(): ApiResult<List<ResponseFriend>> =
        ApiResult.Success(emptyList())

    override suspend fun deleteFriend(userId: String): ApiResult<String> = ApiResult.Success("")

    override suspend fun getBlockedUsers(): ApiResult<List<ResponseBlockedUser>> =
        ApiResult.Success(emptyList())

    override suspend fun blockUser(userId: String): Response<Void> = Response.success(null)

    override suspend fun unblockUser(userId: String): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun getFaqs(): ApiResult<ResponseFaqs> =
        ApiResult.Success(
            body = gson.fromJson(
                readFileContents("mock_faqs.json"),
                ResponseFaqs::class.java
            )
        )

    override suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData> =
        Response.success(
            200,
            ResponseJwtSensitiveUserData("TOKEN")
        )

    override suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData> =
        Response.success(
            200,
            ResponseSensitiveUserData(
                type = "email",
                data = basicProfileDetails.email,
                otpInterval = 300L,
                isOtpBlocked = false,
                isResendOtpBlocked = false,
                otpBlockExpiresIn = null
            )
        )

    override suspend fun getImage(imageUrl: String): ApiResult<ResponseBody> {
        return executeApiServiceCall {
            apiService.getImage(imageUrl)
        }
    }

    override suspend fun markNotificationAsOpened(): ApiResult<String> = ApiResult.Success("")

    override suspend fun getReturnPolicies(): ApiResult<ResponseReturnPolicies> {
        delay(1.5.seconds)

        return ApiResult.Success(
            gson.fromJson(
                readFileContents("mock_return_policies.json"),
                ResponseReturnPolicies::class.java
            )
        )
    }

    private fun readFileContents(fileName: String): BufferedReader {
        return context.assets.open(fileName).bufferedReader()
    }

    private fun readRawTextFile(resId: Int): String {
        return try {
            context.resources.openRawResource(resId).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (_: Exception) {
            "ERROR"
        }
    }
}