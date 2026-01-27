package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.effect.ObserveUiEffect
import com.alexgreece45.marvelx.ui.screens.marketplace.components.CreateListingTextInputFieldView
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.model.CreateListingBasicDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingFieldUi
import com.alexgreece45.marvelx.ui.theme.MarvelXTheme
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.VSpacer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun CreateListingBasicDetailsScreen(
    onEvent: (CreateListingBasicDetailsContract.Event) -> Unit,
    state: CreateListingBasicDetailsContract.State,
    effect: Flow<CreateListingBasicDetailsContract.Effect>,
    goToPreviousStep: () -> Unit,
    setNextButtonEnabled: (Boolean) -> Unit
) {

    ObserveUiEffect(effect) { uiEffect ->
        when (uiEffect) {
            is CreateListingBasicDetailsContract.Effect.GoToPreviousStep -> goToPreviousStep()
            is CreateListingBasicDetailsContract.Effect.SetNextButtonEnabled -> setNextButtonEnabled(
                uiEffect.enabled
            )
        }
    }

    LaunchedEffect(Unit) {
        onEvent(CreateListingBasicDetailsContract.Event.OnResume)
    }

    ContentScreen(
        isLoading = state.isLoading
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp)
            ) {
                CreateListingTextInputFieldView(
                    label = stringResource(R.string.screen_create_listing_basic_details_label_title),
                    placeholder = stringResource(R.string.screen_create_listing_basic_details_placeholder_title),
                    listingFieldUi = state.title,
                    onValueChanged = {
                        onEvent(CreateListingBasicDetailsContract.Event.OnTitleChanged(it))
                    },
                    maxLines = 3
                )

                CreateListingTextInputFieldView(
                    label = stringResource(R.string.screen_create_listing_basic_details_label_description),
                    placeholder = stringResource(R.string.screen_create_listing_basic_details_placeholder_description),
                    listingFieldUi = state.description,
                    onValueChanged = {
                        onEvent(CreateListingBasicDetailsContract.Event.OnDescriptionChanged(it))
                    },
                    minLines = 5,
                    maxLines = 10
                )
            }

            VSpacer.Medium()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenu(
                    label = stringResource(R.string.screen_create_listing_basic_details_label_category),
                    expanded = state.category.isExpanded,
                    modifier = Modifier.weight(1f),
                    value = state.category.value?.label.orEmpty(),
                    enabled = state.wizardMode.canEdit,
                    onDismissRequest = { onEvent(CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnDismissed) },
                    onExpandedChange = { onEvent(CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnClicked) }
                ) {
                    state.listingCategories.forEach { listingCategory ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onEvent(
                                    CreateListingBasicDetailsContract.Event.CategoryDropDownEvents.OnItemSelected(
                                        listingCategory = listingCategory
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = listingCategory.label
                            )
                        }
                    }
                }

                ExposedDropdownMenu(
                    label = stringResource(R.string.screen_create_listing_basic_details_label_subcategory),
                    expanded = state.subcategory.isExpanded,
                    modifier = Modifier.weight(1f),
                    value = state.subcategory.value?.label.orEmpty(),
                    enabled = state.wizardMode.canEdit,
                    onDismissRequest = { onEvent(CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnDismissed) },
                    onExpandedChange = { onEvent(CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnClicked) }
                ) {
                    state.listingSubcategories.forEach { listingSubcategory ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onEvent(
                                    CreateListingBasicDetailsContract.Event.SubcategoryDropDownEvents.OnItemSelected(
                                        listingSubcategory = listingSubcategory
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = listingSubcategory.label
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun CreateListingBasicDetailsScreenPreview() {
    MarvelXTheme {
        CreateListingBasicDetailsScreen(
            onEvent = {},
            state = CreateListingBasicDetailsContract.State(
                isLoading = false,
                title = CreateListingFieldUi.Text(textValue = "Super cool mug!")
            ),
            effect = emptyFlow(),
            goToPreviousStep = {},
            setNextButtonEnabled = {}
        )
    }
}
