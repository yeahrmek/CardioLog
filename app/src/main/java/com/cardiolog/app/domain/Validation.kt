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
                systolic.isBlank() -> "Введите систолическое давление."
                systolicValue == null -> "Систолическое давление должно быть числом."
                systolicValue !in 50..300 -> "Систолическое давление должно быть от 50 до 300."
                else -> null
            },
            diastolicError = when {
                diastolic.isBlank() -> "Введите диастолическое давление."
                diastolicValue == null -> "Диастолическое давление должно быть числом."
                diastolicValue !in 30..200 -> "Диастолическое давление должно быть от 30 до 200."
                else -> null
            },
            pulseError = when {
                pulse.isBlank() -> null
                pulseValue == null -> "Пульс должен быть числом."
                pulseValue !in 30..250 -> "Пульс должен быть от 30 до 250."
                else -> null
            },
        )
    }
}
