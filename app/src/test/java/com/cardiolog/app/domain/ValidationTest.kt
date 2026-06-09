package com.cardiolog.app.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest {
    @Test
    fun validMeasurementPasses() {
        val result = MeasurementValidator.validate("120", "80", "65")
        assertTrue(result.isValid)
        assertNull(result.systolicError)
        assertNull(result.diastolicError)
        assertNull(result.pulseError)
    }

    @Test
    fun optionalPulseCanBeBlank() {
        val result = MeasurementValidator.validate("118", "76", "")
        assertTrue(result.isValid)
    }

    @Test
    fun unreasonableValuesFail() {
        val result = MeasurementValidator.validate("40", "250", "20")
        assertFalse(result.isValid)
        assertTrue(result.systolicError!!.contains("50"))
        assertTrue(result.diastolicError!!.contains("200"))
        assertTrue(result.pulseError!!.contains("30"))
    }
}
