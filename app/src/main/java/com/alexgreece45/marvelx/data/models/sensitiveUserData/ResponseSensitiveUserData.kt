package com.alexgreece45.marvelx.data.models.sensitiveUserData

import android.os.Parcelable
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.OtpPreferredCommunicationType
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Defines the response of a sensitive user data request.
 *
 * @param type  Refers to the type of the retrieved sensitive
 * user data (i.e., email, or phone_number).
 * @param data  Refers to the value of the retrieved sensitive
 * user data (i.e., the stored email address, or phone number).
 */
@Parcelize
data class ResponseSensitiveUserData(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: String
) : Parcelable

fun ResponseSensitiveUserData.toSensitiveUserDataDomain(): SensitiveUserDataDomain =
    SensitiveUserDataDomain(
        type = OtpPreferredCommunicationType.of(this.type),
        data = this.data
    )
