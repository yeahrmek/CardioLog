package com.cardiolog.app.domain

data class BloodPressureMeasurement(
    val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val measuredAtMillis: Long,
    val period: MeasurementPeriod,
    val note: String?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)
