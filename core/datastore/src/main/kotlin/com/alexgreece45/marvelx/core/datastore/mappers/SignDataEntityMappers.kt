package com.alexgreece45.marvelx.core.datastore.mappers

import com.alexgreece45.marvelx.core.datastore.model.SignatureEntity
import com.alexgreece45.marvelx.model.model.Signature

internal fun SignatureEntity.toModel(): Signature =
    Signature(
        key = key,
        algorithm = algorithm
    )

internal fun Signature.toEntity(): SignatureEntity =
    SignatureEntity(
        key = key,
        algorithm = algorithm
    )