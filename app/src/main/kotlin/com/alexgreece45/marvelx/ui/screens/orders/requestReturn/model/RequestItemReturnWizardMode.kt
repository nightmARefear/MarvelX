package com.alexgreece45.marvelx.ui.screens.orders.requestReturn.model

internal sealed interface RequestItemReturnWizardMode {
    data object Create : RequestItemReturnWizardMode
    data object Exists : RequestItemReturnWizardMode
}