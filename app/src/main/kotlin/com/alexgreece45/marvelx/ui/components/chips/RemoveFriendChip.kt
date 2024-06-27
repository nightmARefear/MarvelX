package com.alexgreece45.marvelx.ui.components.chips

import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.extensions.throttledClickable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RemoveFriendChip(onClick: () -> Unit) {
    Chip(
        onClick = {},
        modifier = Modifier.throttledClickable(view = LocalView.current, onClick = onClick),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_remove_friend),
                contentDescription = stringResource(R.string.content_description_remove_friend)
            )
        }
    ) {
        Text(text = stringResource(R.string.generic_chip_label_remove_friend))
    }
}
