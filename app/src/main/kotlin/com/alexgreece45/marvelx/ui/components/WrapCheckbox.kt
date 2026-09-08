package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.alexgreece45.marvelx.common_ui.extensions.clickableNoRipple

@Composable
fun WrapCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.clickableNoRipple(role = Role.Checkbox, onClick = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            enabled = enabled
        )

        Text(
            text = label,
            style = MaterialTheme.typography.body2
        )
    }
}