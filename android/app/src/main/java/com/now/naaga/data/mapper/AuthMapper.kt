package com.now.naaga.data.mapper

import com.now.domain.model.AuthPlatformType
import com.now.domain.model.PlatformAuth
import com.now.naaga.data.remote.dto.PlatformAuthDto

fun PlatformAuthDto.toDomain(): PlatformAuth {
    return PlatformAuth(token, AuthPlatformType.findByName(type))
}

fun PlatformAuth.toDto(): PlatformAuthDto {
    return PlatformAuthDto(token, type.name)
}
