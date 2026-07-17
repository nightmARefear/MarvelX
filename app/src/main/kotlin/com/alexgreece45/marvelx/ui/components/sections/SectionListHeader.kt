package com.alexgreece45.marvelx.ui.components.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.ui.components.icons.NavigateIcon
import com.alexgreece45.marvelx.ui.components.sections.model.SectionHeaderData
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.constants.ConstantsUi

/**
 * Displays a list of section headers.
 */
@Composable
fun SectionListHeader(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    sectionHeadersData: List<SectionHeaderData>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
    ) {
        sectionHeadersData.forEach { sectionHeaderData ->
            SectionHeader(
                title = sectionHeaderData.title,
                leadingIcon = {
                    Icon(
                        painter = painterResource(sectionHeaderData.iconData.iconResource),
                        contentDescription = stringResource(
                            sectionHeaderData.iconData.contentDescription
                        ),
                        modifier = Modifier.size(ConstantsUi.MIN_ICON_SIZE)
                    )
                },
                trailingIcon = {
                    if (sectionHeaderData.isNavigable) {
                        NavigateIcon()
                    }
                },
                onClick = sectionHeaderData.onClick
            )
        }
    }
}
