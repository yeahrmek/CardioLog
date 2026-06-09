package com.cardiolog.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM blood_pressure_measurements ORDER BY measuredAtMillis DESC")
    fun observeAll(): Flow<List<BloodPressureMeasurementEntity>>

    @Query("SELECT * FROM blood_pressure_measurements WHERE id = :id")
    fun observeById(id: Long): Flow<BloodPressureMeasurementEntity?>

    @Query("SELECT * FROM blood_pressure_measurements WHERE measuredAtMillis BETWEEN :startMillis AND :endMillis ORDER BY measuredAtMillis ASC")
    fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<BloodPressureMeasurementEntity>>

    @Upsert
    suspend fun upsert(measurement: BloodPressureMeasurementEntity)

    @Delete
    suspend fun delete(measurement: BloodPressureMeasurementEntity)

    @Query("DELETE FROM blood_pressure_measurements WHERE id = :id")
    suspend fun deleteById(id: Long)
}
