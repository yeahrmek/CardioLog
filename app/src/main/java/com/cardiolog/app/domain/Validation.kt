package com.cardiolog.app.domain

data class MeasurementValidationResult(
    val systolicError: String? = null,
    val diastolicError: String? = null,
    val pulseError: String? = null,
) {
    val isValid: Boolean = systolicError == null && diastolicError == null && pulseError == null
}

object MeasurementValidator {
    fun validate(systolic: String, diastolic: String, pulse: String): MeasurementValidationResult {
        val systolicValue = systolic.toIntOrNull()
        val diastolicValue = diastolic.toIntOrNull()
        val pulseValue = pulse.takeIf { it.isNotBlank() }?.toIntOrNull()
        return MeasurementValidationResult(
            systolicError = when {
                systolic.isBlank() -> "Enter systolic pressure."
                systolicValue == null -> "Systolic pressure must be a number."
                systolicValue !in 50..300 -> "Systolic pressure should be between 50 and 300."
                else -> null
            },
            diastolicError = when {
                diastolic.isBlank() -> "Enter diastolic pressure."
                diastolicValue == null -> "Diastolic pressure must be a number."
                diastolicValue !in 30..200 -> "Diastolic pressure should be between 30 and 200."
                else -> null
            },
            pulseError = when {
                pulse.isBlank() -> null
                pulseValue == null -> "Pulse must be a number."
                pulseValue !in 30..250 -> "Pulse should be between 30 and 250."
                else -> null
            },
        )
    }
}
