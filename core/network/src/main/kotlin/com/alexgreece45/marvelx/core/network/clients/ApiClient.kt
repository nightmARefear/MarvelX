package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.character.CharacterDataWrapper
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.core.network.model.api.device.RequestRegisterDevice
import com.alexgreece45.marvelx.core.network.model.api.device.RequestUpdateDevice
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
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.core.network.model.api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

interface ApiClient {

    suspend fun getCharacters(): Response<CharacterDataWrapper>
    suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper>
    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>
    suspend fun getComicById(comicId: Int): Response<ComicDataWrapper>
    suspend fun signup(requestUserSignup: RequestUserSignup): Response<ResponseSignup>
    suspend fun login(requestUserLogin: RequestUserLogin): Response<ResponseLogin>
    suspend fun loginWithToken(): Response<ResponseLoginWithToken>
    suspend fun postLogin(accessToken: String): Response<ResponsePostLogin>

    suspend fun grandNewAccessToken(
        clientId: String,
        codeVerifier: String,
        code: String,
        uri: String
    ): Response<ResponseToken>

    suspend fun registerDevice(requestRegisterDevice: RequestRegisterDevice): ApiResult<String>
    suspend fun updateDevice(requestUpdateDevice: RequestUpdateDevice): ApiResult<String>
    suspend fun logout(clientId: String, refreshToken: String): ApiResult<String>
    suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess>
    suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess>
    suspend fun forgotPasswordSendOtp(body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp>
    suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail>
    suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber>
    suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp>
    suspend fun forgotPasswordResetPassword(body: RequestForgotPasswordResetPassword): ApiResult<String>
    suspend fun getBasicProfileDetails(userId: String): ApiResult<ResponseBasicProfileDetails>
    suspend fun getCurrentUserProfileDetails(): Response<ResponseBasicProfileDetails>
    suspend fun updateCurrentUserProfileDetails(requestProfileDetailsUpdate: RequestProfileDetailsUpdate): ApiResult<String>
    suspend fun updateUserProfileImage(
        userId: String,
        profileImage: MultipartBody.Part
    ): ApiResult<String>

    suspend fun getUserProfileDetails(userId: String): Response<ResponseProfileDetails>
    suspend fun getUserPrivacySettings(): ApiResult<ResponsePrivacySettings>
    suspend fun updateUserPrivacySettings(requestPrivacySettings: RequestPrivacySettings): ApiResult<String>
    suspend fun getUserFriends(userId: String): ApiResult<List<ResponseFriend>>

    suspend fun getIncomingFriendRequests(): ApiResult<List<ResponseIncomingFriendRequest>>
    suspend fun sendFriendRequest(userId: String): ApiResult<String>
    suspend fun acceptFriendRequest(userId: String): ApiResult<String>
    suspend fun deleteFriendRequest(userId: String, type: String): ApiResult<String>

    suspend fun getFriends(): ApiResult<List<ResponseFriend>>
    suspend fun deleteFriend(userId: String): ApiResult<String>

    suspend fun getBlockedUsers(): ApiResult<List<ResponseBlockedUser>>
    suspend fun blockUser(userId: String): Response<Void>
    suspend fun unblockUser(userId: String): ApiResult<String>

    suspend fun getFaqs(): ApiResult<ResponseFaqs>
    suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData>
    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData>

    suspend fun getImage(imageUrl: String): ApiResult<ResponseBody>

    suspend fun markNotificationAsOpened(): ApiResult<String>

    suspend fun getReturnPolicies(): ApiResult<ResponseReturnPolicies>
}