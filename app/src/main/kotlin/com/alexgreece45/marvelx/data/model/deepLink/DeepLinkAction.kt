package com.alexgreece45.marvelx.data.model.deepLink

import com.google.gson.annotations.SerializedName

/**
 * Defines the deep link action.
 *
 * @param actionType    The type; either [DeepLinkActionType.View] for displaying data or
 * [DeepLinkActionType.Action] for performing an action.
 * @param screenRoute   The exact screen route that the application has to navigate to.
 * @param data          The data that is required by the application.
 */
data class DeepLinkAction(
    @SerializedName("actionType") val actionType: DeepLinkActionType,
    @SerializedName("screenRoute") val screenRoute: String,
    @SerializedName("data") val data: Map<String, Any>
)
