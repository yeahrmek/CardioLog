package com.cardiolog.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cardiolog.app.domain.BloodPressureMeasurement
import com.cardiolog.app.domain.MeasurementPeriod

@Entity(tableName = "blood_pressure_measurements")
data class BloodPressureMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val measuredAtMillis: Long,
    val period: MeasurementPeriod = MeasurementPeriod.Morning,
    val note: String?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
)

fun BloodPressureMeasurementEntity.toDomain() = BloodPressureMeasurement(
    id = id,
    systolic = systolic,
    diastolic = diastolic,
    pulse = pulse,
    measuredAtMillis = measuredAtMillis,
    period = period,
    note = note,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
)

fun BloodPressureMeasurement.toEntity() = BloodPressureMeasurementEntity(
    id = id,
    systolic = systolic,
    diastolic = diastolic,
    pulse = pulse,
    measuredAtMillis = measuredAtMillis,
    period = period,
    note = note,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
)
