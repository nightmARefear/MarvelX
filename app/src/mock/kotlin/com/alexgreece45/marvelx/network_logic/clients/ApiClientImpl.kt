package com.alexgreece45.marvelx.network_logic.clients

import android.content.Context
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.network_logic.models_api.authorize.ResponseAuthorize
import com.alexgreece45.marvelx.network_logic.models_api.character.CharacterDataContainer
import com.alexgreece45.marvelx.network_logic.models_api.character.CharacterDataWrapper
import com.alexgreece45.marvelx.network_logic.models_api.character.comic.ComicDataContainer
import com.alexgreece45.marvelx.network_logic.models_api.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.network_logic.models_api.device.RequestRegisterDevice
import com.alexgreece45.marvelx.network_logic.models_api.device.RequestUpdateDevice
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaqs
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.email.RequestForgotPasswordSendOtp
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.email.RequestForgotPasswordValidateOtp
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.email.ResponseForgotPasswordSendOtp
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.email.ResponseForgotPasswordValidateOtp
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.network_logic.models_api.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.jwtSensitiveUserData.ResponseJwtSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.otp.OtpRequest
import com.alexgreece45.marvelx.network_logic.models_api.otp.OtpResponseSuccess
import com.alexgreece45.marvelx.network_logic.models_api.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.network_logic.models_api.resendOtp.ResponseResendOtpSuccess
import com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import com.alexgreece45.marvelx.network_logic.models_api.user.BodyUserId
import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.network_logic.models_api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.network_logic.models_api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.login.ResponseLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.Sign
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import com.alexgreece45.marvelx.network_logic.services.ApiService
import com.alexgreece45.marvelx.network_logic.util.executeApiServiceCall
import com.alexgreece45.marvelx.network_logic.util.executeApiServiceCallNoTransformation
import com.google.gson.Gson
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
class ApiClientImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val apiService: ApiService,
) : ApiClient {

    private val userId = Random.nextInt(1, 3).toString()
    private val verificationStep = "verification_pin_email"
    private val email = "alexv@domain.com"

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
        apiService.signup(requestUserSignup)

    override suspend fun login(requestUserLogin: RequestUserLogin): Response<ResponseLogin> =
        Response.success(
            200,
            ResponseLogin(
                verificationStep = verificationStep,
                userId = userId
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
                    key = readRawTextFile(R.raw.backend_certificate_https_server),
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
        ResponseToken(
            accessToken = "ACCESS_TOKEN",
            accessExpiresIn = 15,
            refreshToken = "REFRESH_TOKEN",
            refreshExpiresIn = 200,
            tokenType = "access_token"
        )
    )

    override suspend fun registerDevice(requestRegisterDevice: RequestRegisterDevice): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun updateDevice(requestUpdateDevice: RequestUpdateDevice): ApiResult<String> =
        ApiResult.Success("")

    override suspend fun logout(clientId: String, refreshToken: String): Response<Void> =
        apiService.logout(clientId, refreshToken)

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
        apiService.forgotPasswordSendOtp(body)

    override suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail> =
        apiService.forgotPasswordIsOtpBlockedEmail(body)

    override suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber> =
        apiService.forgotPasswordIsOtpBlockedPhoneNumber(body)

    override suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp> =
        apiService.forgotPasswordValidateOtp(body)

    override suspend fun getBasicProfileDetails(userId: String): ApiResult<ResponseBasicProfileDetails> =
        ApiResult.Success(
            ResponseBasicProfileDetails(
                firstName = "John",
                lastName = "Doe",
                gender = "male",
                email = "johndoe@domain.com",
                profileImageUrl = ""
            )
        )

    override suspend fun getCurrentUserProfileDetails(): Response<ResponseBasicProfileDetails> =
        Response.success(
            200,
            ResponseBasicProfileDetails(
                firstName = "Alexandros",
                lastName = "Voliotis",
                gender = "male",
                email = email,
                profileImageUrl = ""
            )
        )

    override suspend fun updateCurrentUserProfileDetails(requestProfileDetailsUpdate: RequestProfileDetailsUpdate): ApiResult<String> =
        executeApiServiceCallNoTransformation {
            apiService.updateCurrentUserProfileDetails(
                requestProfileDetailsUpdate
            )
        }

    override suspend fun updateUserProfileImage(
        userId: String,
        profileImage: MultipartBody.Part
    ): ApiResult<String> =
        executeApiServiceCallNoTransformation {
            apiService.updateUserProfileImage(
                userId,
                profileImage
            )
        }

    override suspend fun getUserProfileDetails(userId: String): Response<ResponseProfileDetails> =
        apiService.getUserProfileDetails(userId = userId)

    override suspend fun getUserPrivacySettings(): ApiResult<ResponsePrivacySettings> =
        executeApiServiceCall { apiService.getUserPrivacySettings() }

    override suspend fun updateUserPrivacySettings(requestPrivacySettings: RequestPrivacySettings): ApiResult<String> =
        executeApiServiceCallNoTransformation {
            apiService.updateUserPrivacySettings(
                requestPrivacySettings
            )
        }

    override suspend fun getUserFriends(userId: String): ApiResult<List<ResponseFriend>> {
        return executeApiServiceCall {
            apiService.getUserFriends(userId)
        }
    }

    override suspend fun getIncomingFriendRequests(): ApiResult<List<ResponseIncomingFriendRequest>> {
        return executeApiServiceCall {
            apiService.getIncomingFriendRequests()
        }
    }

    override suspend fun sendFriendRequest(userId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.sendFriendRequest(BodyUserId(userId))
        }
    }

    override suspend fun acceptFriendRequest(userId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.acceptFriendRequest(BodyUserId(userId))
        }
    }

    override suspend fun deleteFriendRequest(userId: String, type: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.deleteFriendRequest(userId, type)
        }
    }

    override suspend fun getFriends(): ApiResult<List<ResponseFriend>> {
        return executeApiServiceCall {
            apiService.getFriends()
        }
    }

    override suspend fun deleteFriend(userId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.deleteFriend(userId)
        }
    }

    override suspend fun getBlockedUsers(): ApiResult<List<ResponseBlockedUser>> {
        return executeApiServiceCall {
            apiService.getBlockedUsers()
        }
    }

    override suspend fun blockUser(userId: String): Response<Void> {
        return apiService.blockUser(BodyUserId(userId))
    }

    override suspend fun unblockUser(userId: String): ApiResult<String> =
        executeApiServiceCallNoTransformation {
            apiService.unblockUser(userId)
        }

    override suspend fun getFaqs(): ApiResult<ResponseFaqs> =
        executeApiServiceCall { apiService.getFaqs() }

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
                data = email,
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

    override suspend fun markNotificationAsOpened(): ApiResult<String> {
        return executeApiServiceCallNoTransformation {
            apiService.markIncomingFriendRequestNotificationAsOpened()
        }
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
