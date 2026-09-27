package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.character.CharacterDataWrapper
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.core.network.model.api.device.RequestRegisterDevice
import com.alexgreece45.marvelx.core.network.model.api.faq.ResponseFaqs
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.RequestForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.RequestForgotPasswordSendOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.RequestForgotPasswordResetPassword
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.RequestForgotPasswordValidateOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.ResponseForgotPasswordOtpBlockedEmail
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.ResponseForgotPasswordSendOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email.ResponseForgotPasswordValidateOtp
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.phoneNumber.RequestForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.core.network.model.api.forgotPassword.phoneNumber.ResponseForgotPasswordOtpBlockedPhoneNumber
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseReturnPolicies
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseReturnPolicy
import com.alexgreece45.marvelx.core.network.model.api.otp.OtpRequest
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.RequestResendOtp
import com.alexgreece45.marvelx.core.network.model.api.resendOtp.ResponseResendOtpSuccess
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.alexgreece45.marvelx.core.network.model.api.user.BodyUserId
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.core.network.model.api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.core.network.model.api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.core.network.model.api.user.login.ResponseLogin
import com.alexgreece45.marvelx.core.network.model.api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.core.network.model.api.user.postLogin.RequestPostLogin
import com.alexgreece45.marvelx.core.network.model.api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.core.network.model.api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.ApiService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCallNoTransformation
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.koin.core.annotation.Single
import retrofit2.Response

@Single
internal class ApiClientImpl(
    private val apiService: ApiService
) : ApiClient {

    override suspend fun getCharacters(): Response<CharacterDataWrapper> =
        apiService.getCharacters()

    override suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper> =
        apiService.getCharacter(characterId)

    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> =
        apiService.getCharacterComics(characterId)

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> =
        apiService.getComicById(comicId)

    override suspend fun signup(requestUserSignup: RequestUserSignup): Response<ResponseSignup> =
        apiService.signup(requestUserSignup)

    override suspend fun login(requestUserLogin: RequestUserLogin): Response<ResponseLogin> =
        apiService.login(requestUserLogin)

    override suspend fun loginWithToken(): Response<ResponseLoginWithToken> =
        apiService.loginWithToken()

    override suspend fun postLogin(accessToken: String): Response<ResponsePostLogin> =
        apiService.postLogin(RequestPostLogin(accessToken))

    override suspend fun registerDevice(requestRegisterDevice: RequestRegisterDevice): ApiResult<String> =
        executeApiServiceCallNoTransformation { apiService.registerDevice(requestRegisterDevice) }

    override suspend fun logout(clientId: String, refreshToken: String): ApiResult<String> =
        executeApiServiceCallNoTransformation { apiService.logout(clientId, refreshToken) }

    override suspend fun validateOtp(otpRequest: OtpRequest): Response<ResponseToken> =
        apiService.validateOtp(otpRequest)

    override suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess> =
        apiService.resendOtp(requestResendOtp)

    override suspend fun forgotPasswordSendOtp(body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp> =
        apiService.forgotPasswordSendOtp(body)

    override suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail> =
        apiService.forgotPasswordIsOtpBlockedEmail(body)

    override suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber> =
        apiService.forgotPasswordIsOtpBlockedPhoneNumber(body)

    override suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp> =
        apiService.forgotPasswordValidateOtp(body)

    override suspend fun forgotPasswordResetPassword(body: RequestForgotPasswordResetPassword): ApiResult<String> =
        com.alexgreece45.marvelx.core.network.util.executeApiServiceCallNoTransformation {
            apiService.forgotPasswordResetPassword(body)
        }

    override suspend fun getBasicProfileDetails(userId: String): ApiResult<ResponseBasicProfileDetails> =
        executeApiServiceCall { (apiService.getBasicProfileDetails(userId)) }

    override suspend fun getCurrentUserProfileDetails(): Response<ResponseBasicProfileDetails> =
        apiService.getCurrentUserProfileDetails()

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

    override suspend fun getReturnPolicies(): ApiResult<ResponseReturnPolicies> {
        return executeApiServiceCall {
            apiService.getReturnPolicies()
        }
    }
}