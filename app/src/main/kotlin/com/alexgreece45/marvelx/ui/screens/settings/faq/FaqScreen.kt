package com.alexgreece45.marvelx.ui.screens.settings.faq

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ScreenNavigableAction
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.components.effect.OneTimeLaunchedEffect
import com.alexgreece45.marvelx.ui.components.error.ContentErrorScreen
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.components.sections.SectionHeader
import com.alexgreece45.marvelx.ui.screens.settings.faq.model.FaqUi
import com.alexgreece45.marvelx.ui_theme.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.SPACING_LARGE
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.constants.ConstantsUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun FaqScreen(
    onEventSent: (event: FaqContract.Event) -> Unit,
    state: FaqContract.State,
    effect: Flow<FaqContract.Effect>,
    navigateUp: () -> Unit,
) {
    OneTimeLaunchedEffect {
        onEventSent(FaqContract.Event.Init)
    }

    ObserveUiEffect(flow = effect) { uiEffect ->
        when (uiEffect) {
            FaqContract.Effect.Navigation.GoBack -> navigateUp()
        }
    }

    state.error?.let { error ->
        ContentErrorScreen(
            errorTitle = stringResource(R.string.faq_screen_error_loading_faqs),
            errorSubtitle = error.errorSubtitle,
            onClose = { onEventSent(FaqContract.Event.OnBackClicked) },
            errorButtonLayout = ErrorButtonLayout.Single(
                primaryButtonLabel = error.primaryButtonLabel,
                onPrimaryButtonClicked = { onEventSent(error.event) }
            )
        )
        return
    }

    ContentScreen(
        isLoading = state.isLoading,
        onBack = { onEventSent(FaqContract.Event.OnBackClicked) },
        navigableAction = ScreenNavigableAction.Backable(onBack = { onEventSent(FaqContract.Event.OnBackClicked) }),
        showToolbar = true,
        toolbarTitle = stringResource(R.string.faq_screen_toolbar_title)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
        ) {
            if (state.sections.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillParentMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.faq_screen_no_faqs_message),
                            style = MaterialTheme.typography.h5
                        )
                    }
                }
                return@LazyColumn
            }

            items(
                items = state.sections,
                key = { it.id }
            ) { section ->
                FaqSection(
                    section = section,
                    onToggleExpansion = { onEventSent(FaqContract.Event.OnSectionClicked(id = section.id)) }
                )
            }
        }
    }
}

/**
 * Displays a single FAQ section with an expandable/collapsible header.
 *
 * @param section           The data representing the FAQ section.
 * @param onToggleExpansion Callback to be invoked when the section header is clicked to toggle
 * expansion.
 */
@Composable
private fun FaqSection(section: FaqUi, onToggleExpansion: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            FaqSectionHeader(
                title = section.title,
                isExpanded = section.isExpanded,
                onClick = onToggleExpansion
            )

            AnimatedVisibility(
                visible = section.isExpanded,
                modifier = Modifier.padding(
                    start = SPACING_LARGE.dp,
                    top = 0.dp,
                    end = SPACING_LARGE.dp,
                    bottom = SPACING_MEDIUM.dp
                )
            ) {
                Text(
                    text = section.description,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

/**
 * Displays a single header for a FAQ section that can be expanded or collapsed.
 *
 * @param title         The title of the FAQ section.
 * @param isExpanded    Whether the section is currently expanded.
 * @param onClick       Callback to be invoked when the header is clicked.
 */
@Composable
fun FaqSectionHeader(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    SectionHeader(
        title = title,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) {
                    stringResource(R.string.content_description_collapse)
                } else {
                    stringResource(R.string.content_description_expand)
                },
                modifier = Modifier.rotate(
                    degrees = animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(durationMillis = ConstantsUi.FaqScreen.FAQ_SECTION_HEADER_ICON_TWEEN_MILLIS),
                        label = "FAQ Header Icon Rotation" // debugging purposes
                    ).value
                )
            )
        },
        throttleClicks = false,
        showRipple = false,
        onClick = onClick
    )
}

@Preview(
    apiLevel = 34,
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun FaqScreenPreview() {
    MarvelXTheme {
        FaqScreen(
            onEventSent = {},
            state = FaqContract.State(
                isLoading = false,
                sections = listOf(
                    FaqUi(
                        id = "1",
                        title = "1. Title",
                        description = "Description",
                        order = 0,
                        isExpanded = false
                    ),
                    FaqUi(
                        id = "2",
                        title = "2. Title 2",
                        description = "Description 2",
                        order = 0,
                        isExpanded = false
                    ),
                    FaqUi(
                        id = "3",
                        title = "3. Title 3",
                        description = "Description 3",
                        order = 0,
                        isExpanded = false
                    ),
                    FaqUi(
                        id = "4",
                        title = "4. Title 4",
                        description = "Description 4" +
                                "rwerwv34ht435raurmcwe;we" +
                                "rwerwer234k2gnrthrr3234234=542343/*234-*erwerwerwe",
                        order = 0,
                        isExpanded = false
                    ),
                    FaqUi(
                        id = "5",
                        title = "5. Title 5",
                        description = "Description 5" +
                                "rwerwv34ht435rwerwerwerwerwe" +
                                "rwerwer234k2gnrthrr3242432542343/*234-*erwerwerwe" +
                                "rwerwerrrrr3244k2l4,2rr234324rranspovcn ch4r 34erwe",
                        order = 0,
                        isExpanded = false
                    ),
                    FaqUi(
                        id = "6",
                        title = "6. Titlqewewqeqweqweqwqweeweqwqeweqwewqqweewqewe 6",
                        description = "Description 6" +
                                "we" +
                                "rwerwv34ht435rwerwerwerwerwe" +
                                "rwerwerrrrrrrgnrthrr3242432542343/*234-*erwerwerwe" +
                                "rwerwerrrrr3244k2l4,2rrrrrrrrrrwerwerwerwerwe" +
                                "rwerwer4234knv4r23qpr825n44 erwerwerwe" +
                                "rwerwerrrrrraskmdqknfg; 23r9irwerwerwerwerwe",
                        order = 0,
                        isExpanded = true
                    ),
                )
            ),
            effect = emptyFlow(),
            navigateUp = {}
        )
    }
}