package com.alexgreece45.marvelx.ui.screens.notifications.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.extensions.toCustomFormat
import com.alexgreece45.marvelx.core.common.extensions.toZonedDateTime
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.domain.model_api.notification.NotificationDomain
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import com.alexgreece45.marvelx.ui.model_generic.InstantUi
import com.alexgreece45.marvelx.ui_theme.theme.import_aliases.UiThemeDrawable

@Immutable
internal data class NotificationUi(
    val notificationId: String,
    @DrawableRes val iconResId: Int,
    val title: String,
    val body: String,
    val imageUrl: String?,
    /** `true` when [imageUrl] is a person's photo (drawn as a bordered circle), not a listing cover. */
    val isPersonImage: Boolean,
    val createdAt: InstantUi,
    val isRead: Boolean,
    val deepLinkAction: DeepLinkAction
)

/**
 * @param currentYear The year "now" is in; a notification from any other year shows its year too,
 * so an old one never reads like a date from this year.
 */
internal fun NotificationDomain.toUi(
    currentYear: Int = DateUtil.getUTCInstant().toZonedDateTime().year
): NotificationUi =
    NotificationUi(
        notificationId = notificationId,
        iconResId = when (channelType) {
            NotificationChannelType.ListingPurchase -> UiThemeDrawable.ic_money_bag
            NotificationChannelType.OrderStatusChanged -> R.drawable.ic_orders
            NotificationChannelType.IncomingFriendRequest,
            NotificationChannelType.AcceptFriendRequest -> R.drawable.ic_friends
            NotificationChannelType.NewMessage -> R.drawable.ic_chat
        },
        title = title,
        body = body,
        imageUrl = imageUrl,
        isPersonImage = when (channelType) {
            NotificationChannelType.IncomingFriendRequest,
            NotificationChannelType.AcceptFriendRequest,
            NotificationChannelType.NewMessage -> true

            NotificationChannelType.ListingPurchase,
            NotificationChannelType.OrderStatusChanged -> false
        },
        createdAt = InstantUi(
            value = createdAt,
            label = createdAt.toCustomFormat(
                if (createdAt.toZonedDateTime().year == currentYear) "MMM dd, HH:mm" else "MMM dd yyyy, HH:mm"
            )
        ),
        isRead = isRead,
        deepLinkAction = deepLinkAction
    )
