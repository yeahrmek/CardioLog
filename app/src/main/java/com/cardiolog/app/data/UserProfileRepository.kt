package com.cardiolog.app.data

import com.cardiolog.app.domain.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProfileRepository(private val dao: UserProfileDao) {
    fun observeProfile(): Flow<UserProfile> = dao.observeProfile().map { it?.toDomain() ?: UserProfile() }
    suspend fun save(profile: UserProfile) = dao.upsert(profile.toEntity())
}

