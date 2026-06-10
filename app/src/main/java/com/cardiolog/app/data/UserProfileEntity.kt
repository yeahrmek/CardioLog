package com.cardiolog.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cardiolog.app.domain.Sex
import com.cardiolog.app.domain.UserProfile

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1,
    val name: String,
    val age: Int?,
    val sex: Sex,
)

fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    name = name,
    age = age,
    sex = sex,
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    name = name,
    age = age,
    sex = sex,
)

