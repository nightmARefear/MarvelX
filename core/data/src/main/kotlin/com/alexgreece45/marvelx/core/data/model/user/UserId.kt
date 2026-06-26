package com.alexgreece45.marvelx.core.data.model.user

@JvmInline
value class UserId(val value: String) {

    override fun toString(): String = value

    companion object {
        val EMPTY = UserId("")
    }
}