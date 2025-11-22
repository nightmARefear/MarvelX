package com.alexgreece45.marvelx.network_logic.services

import com.alexgreece45.marvelx.network_logic.models_api.authorize.ResponseAuthorize
import com.alexgreece45.marvelx.network_logic.models_api.character.CharacterDataWrapper
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
import com.alexgreece45.marvelx.network_logic.models_api.notification.BodyNotificationId
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
import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.RequestPostLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.ResponseSignup
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    @GET("/v1/public/characters")
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    @GET("/v1/public/characters/{characterId}")
    suspend fun getCharacter(@Path("characterId") characterId: Int): Response<CharacterDataWrapper>

    @GET("/v1/public/characters/{characterId}/comics")
    suspend fun getCharacterComics(@Path("characterId") characterId: Int): Response<ComicDataWrapper>

    @GET("/v1/public/comics/{comicId}")
    suspend fun getComicById(@Path("comicId") comicId: Int): Response<ComicDataWrapper>

    @POST("/api/private/marvelx/user/signup")
    suspend fun signup(@Body requestUserSignup: RequestUserSignup): Response<ResponseSignup>

    @POST("/api/private/marvelx/user/login")
    suspend fun login(@Body requestUserLogin: RequestUserLogin): Response<ResponseLogin>

    @POST("/api/private/marvelx/user/login_with_token")
    suspend fun loginWithToken(): Response<ResponseLoginWithToken>

    @POST("/api/private/marvelx/user/post_login")
    suspend fun postLogin(@Body requestPostLogin: RequestPostLogin): Response<ResponsePostLogin>

    @POST("/api/private/marvelx/user/authorize")
    suspend fun authorize(
        @Query("temp_auth_code") tempAuthCode: String,
        @Query("client_id") clientId: String,
        @Query("code_challenge") codeChallenge: String,
        @Query("code_challenge_method") codeChallengeMethod: String,
    ): Response<ResponseAuthorize>

    @POST("/api/private/marvelx/user/token")
    @FormUrlEncoded
    suspend fun grandNewAccessToken(
        @Field("client_id") clientId: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Response<ResponseToken>

    @POST("/api/private/marvelx/device/register")
    suspend fun registerDevice(@Body requestRegisterDevice: RequestRegisterDevice): Response<Void>

    @POST("/api/private/marvelx/device/update")
    suspend fun updateDevice(@Body requestUpdateDevice: RequestUpdateDevice): Response<Void>

    @POST("/api/private/marvelx/user/logout")
    @FormUrlEncoded
    suspend fun logout(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): Response<Void>

    @POST("/api/private/marvelx/user/validate_otp")
    suspend fun validateOtp(@Body otpRequest: OtpRequest): Response<OtpResponseSuccess>

    @POST("/api/private/marvelx/user/resend_otp")
    suspend fun resendOtp(@Body requestResendOtp: RequestResendOtp): Response<ResponseResendOtpSuccess>

    @POST("/api/private/marvelx/user/forgot_password/send_otp")
    suspend fun forgotPasswordSendOtp(@Body body: RequestForgotPasswordSendOtp): Response<ResponseForgotPasswordSendOtp>

    @POST("/api/private/marvelx/user/forgot_password/otp_blocked/email")
    suspend fun forgotPasswordIsOtpBlockedEmail(@Body body: RequestForgotPasswordOtpBlockedEmail): Response<ResponseForgotPasswordOtpBlockedEmail>

    @POST("/api/private/marvelx/user/forgot_password/otp_blocked/phone_number")
    suspend fun forgotPasswordIsOtpBlockedPhoneNumber(@Body body: RequestForgotPasswordOtpBlockedPhoneNumber): Response<ResponseForgotPasswordOtpBlockedPhoneNumber>

    @POST("/api/private/marvelx/user/forgot_password/validate_otp")
    suspend fun forgotPasswordValidateOtp(@Body body: RequestForgotPasswordValidateOtp): Response<ResponseForgotPasswordValidateOtp>

    /**
     * Retrieves the __public__ profile details of another user with the ID of [userId].
     */
    @GET("/api/private/marvelx/user/{userId}/profile")
    suspend fun getUserProfileDetails(@Path("userId") userId: String): Response<ResponseProfileDetails>

    /**
     * Retrieves the friends of another user.
     */
    @GET("/api/private/marvelx/user/{userId}/friends")
    suspend fun getUserFriends(@Path("userId") userId: String): Response<List<ResponseFriend>>

    /**
     * Retrieves the profile details of the current user.
     */
    @GET("/api/private/marvelx/user/me/profile")
    suspend fun getCurrentUserProfileDetails(): Response<ResponseBasicProfileDetails>

    /**
     * Updates the profile details of the current user.
     */
    @PUT("/api/private/marvelx/user/me/profile")
    suspend fun updateCurrentUserProfileDetails(@Body requestProfileDetailsUpdate: RequestProfileDetailsUpdate): Response<Void>

    /**
     * Updates the profile image of the current user.
     */
    @Multipart
    @PUT("/api/private/marvelx/user/me/{userId}/profile-image")
    suspend fun updateUserProfileImage(
        @Path("userId") userId: String,
        @Part profileImage: MultipartBody.Part
    ): Response<Void>

    /**
     * Retrieves the blocked users of the current one.
     */
    @GET("/api/private/marvelx/user/me/blocked")
    suspend fun getBlockedUsers(): Response<List<ResponseBlockedUser>>

    /**
     * Retrieves the privacy settings of the current user.
     */
    @GET("/api/private/marvelx/user/me/privacy-settings")
    suspend fun getUserPrivacySettings(): Response<ResponsePrivacySettings>

    /**
     * Updates the privacy settings of the current user.
     */
    @PUT("/api/private/marvelx/user/me/privacy-settings")
    suspend fun updateUserPrivacySettings(@Body requestPrivacySettings: RequestPrivacySettings): Response<Void>

    /**
     * Retrieves the friends of the current user.
     */
    @GET("/api/private/marvelx/user/me/friends")
    suspend fun getFriends(): Response<List<ResponseFriend>>

    /**
     * Retrieves the incoming friend requests of the current user.
     */
    @GET("/api/private/marvelx/user/me/friend-requests")
    suspend fun getIncomingFriendRequests(): Response<List<ResponseIncomingFriendRequest>>

    /**
     * Sends a friend request to another user.
     */
    @POST("/api/private/marvelx/user/friend-requests")
    suspend fun sendFriendRequest(@Body userId: BodyUserId): Response<Void>

    /**
     * Accepts the friend request of another user.
     */
    @PATCH("/api/private/marvelx/user/friend-requests")
    suspend fun acceptFriendRequest(@Body userId: BodyUserId): Response<Void>

    /**
     * Deletes the friend request of another user.
     */
    @DELETE("/api/private/marvelx/user/{userId}/friend-requests")
    suspend fun deleteFriendRequest(
        @Path("userId") userId: String,
        @Query("deleteFriendRequestType") deleteFriendRequestType: String
    ): Response<Void>

    @DELETE("/api/private/marvelx/user/{userId}/friend")
    suspend fun deleteFriend(@Path("userId") userId: String): Response<Void>

    /**
     * Blocks the user with the given [userId].
     */
    @POST("/api/private/marvelx/user/blocks")
    suspend fun blockUser(@Body userId: BodyUserId): Response<Void>

    /**
     * Unblocks the user with the given [userId].
     */
    @DELETE("/api/private/marvelx/user/{userId}/blocks")
    suspend fun unblockUser(@Path("userId") userId: String): Response<Void>

    /**
     * Retrieves the FAQs.
     */
    @GET("/api/private/marvelx/faqs")
    suspend fun getFaqs(): Response<ResponseFaqs>

    @POST("/api/private/marvelx/user/jwt_sensitive_data")
    suspend fun getJwtForSensitiveUserData(@Body requestJwtSensitiveUserData: RequestJwtSensitiveUserData): Response<ResponseJwtSensitiveUserData>

    @POST("/api/private/marvelx/user/sensitive_data")
    suspend fun getSensitiveUserData(@Body requestSensitiveUserData: RequestSensitiveUserData): Response<ResponseSensitiveUserData>

    @GET
    @Streaming
    suspend fun getImage(@Url url: String): Response<ResponseBody>

    @PATCH("/api/private/marvelx/notifications")
    suspend fun markNotificationAsOpened(@Body bodyNotificationID: BodyNotificationId): Response<Void>
}
