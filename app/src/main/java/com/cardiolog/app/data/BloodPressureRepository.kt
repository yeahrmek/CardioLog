package com.cardiolog.app.data

import com.cardiolog.app.domain.BloodPressureMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BloodPressureRepository(private val dao: BloodPressureDao) {
    fun observeAll(): Flow<List<BloodPressureMeasurement>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }
    fun observeById(id: Long): Flow<BloodPressureMeasurement?> = dao.observeById(id).map { it?.toDomain() }
    fun observeBetween(startMillis: Long, endMillis: Long): Flow<List<BloodPressureMeasurement>> =
        dao.observeBetween(startMillis, endMillis).map { rows -> rows.map { it.toDomain() } }
    suspend fun save(measurement: BloodPressureMeasurement) = dao.upsert(measurement.toEntity())
    suspend fun delete(id: Long) = dao.deleteById(id)
}
