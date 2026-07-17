package com.alexgreece45.marvelx.core.datastore.mappers

import com.alexgreece45.marvelx.core.datastore.model.UserIdEntity
import com.alexgreece45.marvelx.model.model.user.UserId

internal fun UserIdEntity.toModel(): UserId = UserId(this.userId)

internal fun UserId.toEntity(): UserIdEntity = UserIdEntity(this.value)