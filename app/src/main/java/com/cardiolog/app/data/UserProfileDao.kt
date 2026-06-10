package com.cardiolog.app.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Upsert
    suspend fun upsert(profile: UserProfileEntity)
}

