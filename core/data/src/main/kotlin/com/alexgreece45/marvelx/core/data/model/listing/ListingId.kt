package com.alexgreece45.marvelx.core.data.model.listing

@JvmInline
value class ListingId(val value: String) {

    override fun toString(): String = value

    companion object {
        val EMPTY = ListingId("")
    }
}