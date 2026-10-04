package com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.data.model.marketplace.listing.EnumGeneric
import com.alexgreece45.marvelx.ui.components.ContentScreen
import com.alexgreece45.marvelx.ui.components.ExposedDropdownMenu
import com.alexgreece45.marvelx.ui.components.OutlinedTextFieldValidation
import com.alexgreece45.marvelx.ui.components.WrapCheckbox
import com.alexgreece45.marvelx.ui.screens.marketplace.components.WizardTextInputField
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsContract
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.model.CreateListingAdvancedDetailsScreenData
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.model.CreateListingWizardMode
import com.alexgreece45.marvelx.ui.screens.marketplace.model.ListingDetailsUi
import com.alexgreece45.marvelx.util.SPACING_MEDIUM
import com.alexgreece45.marvelx.util.VSpacer

@Composable
internal fun CreateListingAdvancedDetailsScreen(
    onEvent: (CreateListingAdvancedDetailsContract.Event) -> Unit,
    state: CreateListingAdvancedDetailsContract.State,
    effect: SingleObserverFlow<CreateListingAdvancedDetailsContract.Effect>,
    showErrorSnackbar: (errorMessage: String) -> Unit,
    goToPreviousStep: () -> Unit,
    setNextButtonEnabled: (Boolean) -> Unit,
    createListingAdvancedDetailsScreenData: CreateListingAdvancedDetailsScreenData
) {

    ObserveUiEffect(flow = effect, tag = "CreateListingAdvancedDetailsScreen") { uiEffect ->
        when (uiEffect) {
            CreateListingAdvancedDetailsContract.Effect.GoToPreviousStep -> goToPreviousStep()
            is CreateListingAdvancedDetailsContract.Effect.SetNextButtonEnabled -> setNextButtonEnabled(
                uiEffect.enabled
            )

            is CreateListingAdvancedDetailsContract.Effect.ShowErrorSnackbar -> {
                showErrorSnackbar(uiEffect.errorMessage)
            }
        }
    }

    LaunchedEffect(Unit) {
        onEvent(
            CreateListingAdvancedDetailsContract.Event.Init(
                createListingAdvancedDetailsScreenData
            )
        )
    }

    ContentScreen(
        isLoading = state.isLoading,
        isInnerScreen = true
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
                when (val listingDetails = state.listingDetails) {
                    is ListingDetailsUi.ApparelDetailsUi ->
                        ApparelDetailsView(
                            apparelDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onDismissRequest = { onEvent(CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnSizeDropDownOnDismissed) },
                            onExpandedChange = { onEvent(CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnSizeDropDownClicked) },
                            onDropDownMenuItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnSizeDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onColorChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Apparel.OnColorChanged(
                                        it
                                    )
                                )
                            }
                        )

                    is ListingDetailsUi.DrinkwareDetailsUi ->
                        DrinkwareDetailsView(
                            drinkwareDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onDismissRequest = {
                                onEvent(CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnMaterialDropDownOnDismissed)
                            },
                            onExpandedChange = {
                                onEvent(CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnMaterialDropDownClicked)
                            },
                            onDropDownMenuItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnMaterialDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onCapacityMlChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnCapacityMlChanged(
                                        it
                                    )
                                )
                            },
                            onDishwasherSafeCheckboxChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Drinkware.OnDishwasherSafeCheckboxChanged
                                )
                            }
                        )

                    is ListingDetailsUi.CollectibleDetailsUi ->
                        CollectibleDetailsView(
                            collectibleDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onScaleDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnScaleDropDownOnDismissed
                                )
                            },
                            onScaleDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnScaleDropDownClicked
                                )
                            },
                            onScaleDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnScaleDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onBoxConditionDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnBoxConditionDropDownOnDismissed
                                )
                            },
                            onBoxConditionDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnBoxConditionDropDownClicked
                                )
                            },
                            onBoxConditionDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Collectible.OnBoxConditionDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                        )

                    is ListingDetailsUi.ArtDetailsUi ->
                        ArtDetailsView(
                            artDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onWidthChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Art.OnDimensionsWidthChanged(
                                        it
                                    )
                                )
                            },
                            onHeightChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Art.OnDimensionsHeightChanged(
                                        it
                                    )
                                )
                            },
                            onFramedCheckedChanged = { onEvent(CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Art.OnFramedCheckboxChanged) }
                        )

                    is ListingDetailsUi.AccessoryDetailsUi ->
                        AccessoryDetailsView(
                            accessoryDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onMaterialDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnMaterialDropDownOnDismissed
                                )
                            },
                            onMaterialDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnMaterialDropDownClicked
                                )
                            },
                            onMaterialDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnMaterialDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onColorChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Accessory.OnColorChanged(
                                        it
                                    )
                                )
                            }
                        )

                    is ListingDetailsUi.HomeDetailsUi ->
                        HomeDetailsView(
                            homeDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onWidthChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Home.OnDimensionsWidthChanged(
                                        it
                                    )
                                )
                            },
                            onHeightChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Home.OnDimensionsHeightChanged(
                                        it
                                    )
                                )
                            }
                        )

                    is ListingDetailsUi.ComicDetailsUi ->
                        ComicDetailsView(
                            comicDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onGradeDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnGradeDropDownOnDismissed
                                )
                            },
                            onGradeDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnGradeDropDownClicked
                                )
                            },
                            onGradeDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnGradeDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onPublisherChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnPublisherChanged(
                                        it
                                    )
                                )
                            },
                            onYearChanged = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.Comic.OnYearChanged(
                                        it
                                    )
                                )
                            },
                        )

                    is ListingDetailsUi.CardDetailsUi ->
                        CardDetailsView(
                            cardDetails = listingDetails,
                            canEdit = state.wizardMode.canEdit,
                            onGradeDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnGradeDropDownOnDismissed
                                )
                            },
                            onGradeDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnGradeDropDownClicked
                                )
                            },
                            onGradeDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnGradeDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onRarityDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnRarityDropDownOnDismissed
                                )
                            },
                            onRarityDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnRarityDropDownClicked
                                )
                            },
                            onRarityDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnRarityDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                            onVariantDropDownDismissed = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnVariantDropDownOnDismissed
                                )
                            },
                            onVariantDropDownExpandedChange = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnVariantDropDownClicked
                                )
                            },
                            onVariantDropDownItemSelected = {
                                onEvent(
                                    CreateListingAdvancedDetailsContract.Event.ListingDetailsEvents.CardDetails.OnVariantDropDownItemSelected(
                                        it
                                    )
                                )
                            },
                        )

                    null -> {}
                }
            }

            // Condition.
            ExposedDropdownMenu(
                label = stringResource(R.string.screen_create_listing_advanced_details_label_condition),
                expanded = state.condition.isExpanded,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.CONDITION_DROPDOWN),
                value = state.condition.value?.label,
                enabled = state.wizardMode.canEdit,
                onDismissRequest = {
                    onEvent(CreateListingAdvancedDetailsContract.Event.ConditionDropDownEvents.OnDismissed)
                },
                onExpandedChange = {
                    onEvent(CreateListingAdvancedDetailsContract.Event.ConditionDropDownEvents.OnClicked)

                }
            ) {
                state.conditions.forEach { condition ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(CreateListingAdvancedDetailsTestTags.conditionOption(condition.code)),
                        onClick = {
                            onEvent(
                                CreateListingAdvancedDetailsContract.Event.ConditionDropDownEvents.OnItemSelected(
                                    newCondition = condition
                                )
                            )
                        }
                    ) {
                        Text(
                            text = condition.label
                        )
                    }
                }
            }

            VSpacer.Medium()

            PriceView(
                priceField = state.price,
                onValueChanged = {
                    onEvent(CreateListingAdvancedDetailsContract.Event.OnPriceChanged(it))
                },
                canEdit = state.wizardMode.canEdit
            )

            // Final sale can only be set on creation (the update call doesn't accept it).
            if (state.wizardMode == CreateListingWizardMode.CREATE) {
                VSpacer.Medium()

                WrapCheckbox(
                    label = stringResource(R.string.screen_create_listing_advanced_details_label_final_sale),
                    checked = state.finalSale,
                    onCheckedChange = {
                        onEvent(CreateListingAdvancedDetailsContract.Event.OnFinalSaleCheckboxChanged)
                    },
                    modifier = Modifier.testTag(CreateListingAdvancedDetailsTestTags.FINAL_SALE_CHECKBOX)
                )
            }
        }
    }
}

@Composable
private fun ApparelDetailsView(
    apparelDetails: ListingDetailsUi.ApparelDetailsUi,
    canEdit: Boolean,
    onDismissRequest: () -> Unit,
    onExpandedChange: () -> Unit,
    onDropDownMenuItemSelected: (EnumGeneric) -> Unit,
    onColorChanged: (String) -> Unit
) {
    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_apparel_size),
        expanded = apparelDetails.size.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.APPAREL_SIZE_DROPDOWN),
        value = apparelDetails.size.value?.label,
        enabled = canEdit,
        onDismissRequest = onDismissRequest,
        onExpandedChange = onExpandedChange
    ) {
        apparelDetails.sizes.forEach { size ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.apparelSizeOption(size.code)),
                onClick = {
                    onDropDownMenuItemSelected(size)
                }
            ) {
                Text(
                    text = size.label
                )
            }
        }
    }

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_color),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_color),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.APPAREL_COLOR_TEXT_FIELD),
        wizardInputField = apparelDetails.color,
        onValueChanged = onColorChanged
    )
}

@Composable
private fun DrinkwareDetailsView(
    drinkwareDetails: ListingDetailsUi.DrinkwareDetailsUi,
    canEdit: Boolean,
    onDismissRequest: () -> Unit,
    onExpandedChange: () -> Unit,
    onDropDownMenuItemSelected: (EnumGeneric) -> Unit,
    onCapacityMlChanged: (String) -> Unit,
    onDishwasherSafeCheckboxChanged: () -> Unit
) {
    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_material),
        expanded = drinkwareDetails.material.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.DRINKWARE_MATERIAL_DROPDOWN),
        value = drinkwareDetails.material.value?.label,
        enabled = canEdit,
        onDismissRequest = onDismissRequest,
        onExpandedChange = onExpandedChange
    ) {
        drinkwareDetails.materials.forEach { material ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.drinkwareMaterialOption(material.code)),
                onClick = {
                    onDropDownMenuItemSelected(material)
                }
            ) {
                Text(
                    text = material.label
                )
            }
        }
    }

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_capacity),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_capacity),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.DRINKWARE_CAPACITY_TEXT_FIELD),
        wizardInputField = drinkwareDetails.capacityMl,
        onValueChanged = onCapacityMlChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
    )

    WrapCheckbox(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_dishwasher_safe),
        checked = drinkwareDetails.dishwasherSafe,
        onCheckedChange = onDishwasherSafeCheckboxChanged,
        enabled = canEdit,
        modifier = Modifier.testTag(CreateListingAdvancedDetailsTestTags.DRINKWARE_DISHWASHER_SAFE_CHECKBOX)
)
}

@Composable
private fun CollectibleDetailsView(
    collectibleDetails: ListingDetailsUi.CollectibleDetailsUi,
    canEdit: Boolean,
    onScaleDropDownDismissed: () -> Unit,
    onScaleDropDownExpandedChange: () -> Unit,
    onScaleDropDownItemSelected: (EnumGeneric) -> Unit,
    onBoxConditionDropDownDismissed: () -> Unit,
    onBoxConditionDropDownExpandedChange: () -> Unit,
    onBoxConditionDropDownItemSelected: (EnumGeneric) -> Unit,
) {
    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_scale),
        expanded = collectibleDetails.scale.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.COLLECTIBLE_SCALE_DROPDOWN),
        value = collectibleDetails.scale.value?.label,
        enabled = canEdit,
        onDismissRequest = onScaleDropDownDismissed,
        onExpandedChange = onScaleDropDownExpandedChange
    ) {
        collectibleDetails.scales.forEach { scale ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.collectibleScaleOption(scale.code)),
                onClick = {
                    onScaleDropDownItemSelected(scale)
                }
            ) {
                Text(
                    text = scale.label
                )
            }
        }
    }

    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_box_condition),
        expanded = collectibleDetails.boxCondition.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.COLLECTIBLE_BOX_CONDITION_DROPDOWN),
        value = collectibleDetails.boxCondition.value?.label,
        enabled = canEdit,
        onDismissRequest = onBoxConditionDropDownDismissed,
        onExpandedChange = onBoxConditionDropDownExpandedChange
    ) {
        collectibleDetails.boxConditions.forEach { boxCondition ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.collectibleBoxConditionOption(boxCondition.code)),
                onClick = {
                    onBoxConditionDropDownItemSelected(boxCondition)
                }
            ) {
                Text(
                    text = boxCondition.label
                )
            }
        }
    }
}

@Composable
private fun ArtDetailsView(
    artDetails: ListingDetailsUi.ArtDetailsUi,
    canEdit: Boolean,
    onWidthChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onFramedCheckedChanged: () -> Unit
) {
    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_width),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_width),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.ART_WIDTH_TEXT_FIELD),
        wizardInputField = artDetails.dimensions.width,
        onValueChanged = onWidthChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
    )

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_height),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_height),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.ART_HEIGHT_TEXT_FIELD),
        wizardInputField = artDetails.dimensions.height,
        onValueChanged = onHeightChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
    )

    WrapCheckbox(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_framed),
        checked = artDetails.framed,
        onCheckedChange = { onFramedCheckedChanged() },
        modifier = Modifier.testTag(CreateListingAdvancedDetailsTestTags.ART_FRAMED_CHECKBOX)
    )
}

@Composable
private fun AccessoryDetailsView(
    accessoryDetails: ListingDetailsUi.AccessoryDetailsUi,
    canEdit: Boolean,
    onMaterialDropDownDismissed: () -> Unit,
    onMaterialDropDownExpandedChange: () -> Unit,
    onMaterialDropDownItemSelected: (EnumGeneric) -> Unit,
    onColorChanged: (String) -> Unit
) {
    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_material),
        expanded = accessoryDetails.material.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.ACCESSORY_MATERIAL_DROPDOWN),
        value = accessoryDetails.material.value?.label,
        enabled = canEdit,
        onDismissRequest = onMaterialDropDownDismissed,
        onExpandedChange = onMaterialDropDownExpandedChange
    ) {
        accessoryDetails.materials.forEach { material ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.accessoryMaterialOption(material.code)),
                onClick = {
                    onMaterialDropDownItemSelected(material)
                }
            ) {
                Text(
                    text = material.label
                )
            }
        }
    }

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_color),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_color),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.ACCESSORY_COLOR_TEXT_FIELD),
        wizardInputField = accessoryDetails.color,
        onValueChanged = onColorChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Composable
private fun HomeDetailsView(
    homeDetails: ListingDetailsUi.HomeDetailsUi,
    canEdit: Boolean,
    onWidthChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
) {
    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_width),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_width),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.HOME_WIDTH_TEXT_FIELD),
        wizardInputField = homeDetails.dimensions.width,
        onValueChanged = onWidthChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
    )

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_height),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_height),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.HOME_HEIGHT_TEXT_FIELD),
        wizardInputField = homeDetails.dimensions.height,
        onValueChanged = onHeightChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
    )
}

@Composable
private fun ComicDetailsView(
    comicDetails: ListingDetailsUi.ComicDetailsUi,
    canEdit: Boolean,
    onGradeDropDownDismissed: () -> Unit,
    onGradeDropDownExpandedChange: () -> Unit,
    onGradeDropDownItemSelected: (EnumGeneric) -> Unit,
    onPublisherChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit
) {
    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_grade),
        expanded = comicDetails.grade.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.COMIC_GRADE_DROPDOWN),
        value = comicDetails.grade.value?.label,
        enabled = canEdit,
        onDismissRequest = onGradeDropDownDismissed,
        onExpandedChange = onGradeDropDownExpandedChange
    ) {
        comicDetails.grades.forEach { grade ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.comicGradeOption(grade.code)),
                onClick = {
                    onGradeDropDownItemSelected(grade)
                }
            ) {
                Text(
                    text = grade.label
                )
            }
        }
    }

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_publisher),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_publisher),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.COMIC_PUBLISHER_TEXT_FIELD),
        wizardInputField = comicDetails.publisher,
        onValueChanged = onPublisherChanged,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
    )

    WizardTextInputField(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_year),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_year),
        enabled = canEdit,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.COMIC_YEAR_TEXT_FIELD),
        wizardInputField = comicDetails.year,
        onValueChanged = onYearChanged,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
    )
}

@Composable
private fun CardDetailsView(
    cardDetails: ListingDetailsUi.CardDetailsUi,
    canEdit: Boolean,
    onGradeDropDownDismissed: () -> Unit,
    onGradeDropDownExpandedChange: () -> Unit,
    onGradeDropDownItemSelected: (EnumGeneric) -> Unit,
    onRarityDropDownDismissed: () -> Unit,
    onRarityDropDownExpandedChange: () -> Unit,
    onRarityDropDownItemSelected: (EnumGeneric) -> Unit,
    onVariantDropDownDismissed: () -> Unit,
    onVariantDropDownExpandedChange: () -> Unit,
    onVariantDropDownItemSelected: (EnumGeneric) -> Unit,
) {
    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_grade),
        expanded = cardDetails.grade.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.CARD_GRADE_DROPDOWN),
        value = cardDetails.grade.value?.label,
        enabled = canEdit,
        onDismissRequest = onGradeDropDownDismissed,
        onExpandedChange = onGradeDropDownExpandedChange
    ) {
        cardDetails.grades.forEach { grade ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.cardGradeOption(grade.code)),
                onClick = {
                    onGradeDropDownItemSelected(grade)
                }
            ) {
                Text(
                    text = grade.label
                )
            }
        }
    }

    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_rarity),
        expanded = cardDetails.rarity.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.CARD_RARITY_DROPDOWN),
        value = cardDetails.rarity.value?.label,
        enabled = canEdit,
        onDismissRequest = onRarityDropDownDismissed,
        onExpandedChange = onRarityDropDownExpandedChange
    ) {
        cardDetails.rarities.forEach { rarity ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.cardRarityOption(rarity.code)),
                onClick = {
                    onRarityDropDownItemSelected(rarity)
                }
            ) {
                Text(
                    text = rarity.label
                )
            }
        }
    }

    ExposedDropdownMenu(
        label = stringResource(R.string.screen_create_listing_advanced_details_label_variant),
        expanded = cardDetails.variant.isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.CARD_VARIANT_DROPDOWN),
        value = cardDetails.variant.value?.label,
        enabled = canEdit,
        onDismissRequest = onVariantDropDownDismissed,
        onExpandedChange = onVariantDropDownExpandedChange
    ) {
        cardDetails.variants.forEach { variant ->
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CreateListingAdvancedDetailsTestTags.cardVariantOption(variant.code)),
                onClick = {
                    onVariantDropDownItemSelected(variant)
                }
            ) {
                Text(
                    text = variant.label
                )
            }
        }
    }
}

@Composable
private fun PriceView(
    priceField: CreateListingAdvancedDetailsContract.PriceField,
    onValueChanged: (String) -> Unit,
    canEdit: Boolean
) {
    OutlinedTextFieldValidation(
        value = priceField.value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CreateListingAdvancedDetailsTestTags.PRICE_TEXT_FIELD),
        enabled = canEdit,
        label = stringResource(R.string.screen_create_listing_advanced_details_label_price),
        placeholder = stringResource(R.string.screen_create_listing_advanced_details_placeholder_price),
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_euro),
                contentDescription = stringResource(R.string.screen_create_listing_advanced_details_content_description_euro)
            )
        },
        errorText = priceField.validationResult.message,
        isError = !priceField.validationResult.isValid,
    )
}