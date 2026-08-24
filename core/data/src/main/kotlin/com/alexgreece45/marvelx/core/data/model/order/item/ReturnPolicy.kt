package com.alexgreece45.marvelx.core.data.model.order.item

import com.alexgreece45.marvelx.core.common.extensions.toImmutableListOrEmpty
import com.alexgreece45.marvelx.core.network.model.api.order.item.ResponseReturnPolicy
import com.google.errorprone.annotations.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class ReturnPolicy(
    val header: String,
    val description: String,
    val items: ImmutableList<String>
)

fun ResponseReturnPolicy.toData(): ReturnPolicy =
    ReturnPolicy(
        header = header,
        description = description,
        items = items.toImmutableListOrEmpty()
    )