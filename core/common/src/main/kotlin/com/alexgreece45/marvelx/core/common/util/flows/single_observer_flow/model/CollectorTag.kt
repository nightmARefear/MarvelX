package com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.model

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

// This acts as an invisible tag we can attach to any coroutine
data class CollectorTag(val name: String) : AbstractCoroutineContextElement(CollectorTag) {
    // The Key allows us to look up this specific element inside the context later
    companion object Key : CoroutineContext.Key<CollectorTag>
}