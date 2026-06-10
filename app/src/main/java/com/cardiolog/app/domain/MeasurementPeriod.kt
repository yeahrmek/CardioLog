package com.cardiolog.app.domain

import java.time.LocalDateTime

enum class MeasurementPeriod(val title: String) {
    Night("Ночь"),
    Morning("Утро"),
    Day("День"),
    Evening("Вечер"),
}

fun LocalDateTime.toMeasurementPeriod(): MeasurementPeriod = when (hour) {
    in 6..11 -> MeasurementPeriod.Morning
    in 12..17 -> MeasurementPeriod.Day
    in 18..23 -> MeasurementPeriod.Evening
    else -> MeasurementPeriod.Night
}
