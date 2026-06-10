package com.cardiolog.app.domain

data class UserProfile(
    val id: Long = 1,
    val name: String = "",
    val age: Int? = null,
    val sex: Sex = Sex.NotSpecified,
)

enum class Sex(val title: String) {
    NotSpecified("Не указано"),
    Female("Женский"),
    Male("Мужской"),
}

