package com.alexgreece45.marvelx.network_logic.api

import com.alexgreece45.marvelx.network_logic.models_api.authorize.ResponseAuthorize
import com.alexgreece45.marvelx.network_logic.models_api.character.CharacterDataWrapper
import com.alexgreece45.marvelx.network_logic.models_api.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.network_logic.models_api.faq.ResponseFaq
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
import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.network_logic.models_api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.network_logic.models_api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.login.ResponseLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import okhttp3.MultipartBody
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

    suspend fun authorize(
        tempAuthCode: String,
        clientId: String,
        codeChallenge: String,
        codeChallengeMethod: String
    ): Response<ResponseAuthorize>

    suspend fun grandNewAccessToken(
        clientId: String,
        codeVerifier: String,
        code: String,
        uri: String
    ): Response<ResponseToken>

    suspend fun logout(clientId: String, refreshToken: String): Response<Void>
    suspend fun validateOtp(otpRequest: OtpRequest): Response<OtpResponseSuccess>
    suspend fun resendOtp(requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess>
    suspend fun forgotPasswordSendOtp(body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp>
    suspend fun forgotPasswordIsOtpBlockedEmail(body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail>
    suspend fun forgotPasswordIsOtpBlockedPhoneNumber(body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber>
    suspend fun forgotPasswordValidateOtp(body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp>
    suspend fun getCurrentUserProfileDetails(): Response<ResponseBasicProfileDetails>
    suspend fun updateCurrentUserProfileDetails(requestProfileDetailsUpdate: RequestProfileDetailsUpdate): ApiResult<String>
    suspend fun updateUserProfileImage(profileImage: MultipartBody.Part): ApiResult<String>
    suspend fun getUserProfileDetails(userId: String): Response<ResponseProfileDetails>
    suspend fun getUserPrivacySettings(): ApiResult<ResponsePrivacySettings>
    suspend fun updateUserPrivacySettings(requestPrivacySettings: RequestPrivacySettings): ApiResult<String>
    suspend fun getUserFriends(userId: String): ApiResult<List<ResponseFriend>>

    suspend fun getIncomingFriendRequests(): ApiResult<List<ResponseIncomingFriendRequest>>
    suspend fun sendFriendRequest(userId: String): ApiResult<String>
    suspend fun acceptFriendRequest(userId: String): ApiResult<String>
    suspend fun deleteFriendRequest(userId: String): ApiResult<String>

    suspend fun getFriends(): ApiResult<List<ResponseFriend>>
    suspend fun deleteFriend(userId: String): ApiResult<String>

    suspend fun getBlockedUsers(): ApiResult<List<ResponseBlockedUser>>
    suspend fun blockUser(userId: String): Response<Void>
    suspend fun unblockUser(userId: String): ApiResult<String>

    suspend fun getFaqs(): ApiResult<ResponseFaq>
    suspend fun getJwtForSensitiveUserData(requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData>
    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData>
}
