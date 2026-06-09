package com.cardiolog.app.ui.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.cardiolog.app.CardioLogApplication
import com.cardiolog.app.data.BloodPressureRepository
import com.cardiolog.app.domain.BloodPressureMeasurement
import com.cardiolog.app.domain.MeasurementValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SaveStatus { data object Idle : SaveStatus; data object Saved : SaveStatus }

data class AddMeasurementUiState(
    val measurementId: Long? = null,
    val systolic: String = "",
    val diastolic: String = "",
    val pulse: String = "",
    val measuredAtMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val systolicError: String? = null,
    val diastolicError: String? = null,
    val pulseError: String? = null,
    val isEditMode: Boolean = false,
    val saveStatus: SaveStatus = SaveStatus.Idle,
)

class AddMeasurementViewModel(
    private val repository: BloodPressureRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddMeasurementUiState())
    val uiState: StateFlow<AddMeasurementUiState> = _uiState.asStateFlow()

    init {
        val id = savedStateHandle.get<Long>("measurementId") ?: -1L
        if (id > 0) loadMeasurement(id)
    }

    private fun loadMeasurement(id: Long) = viewModelScope.launch {
        val measurement = repository.observeById(id).filterNotNull().first()
        _uiState.value = AddMeasurementUiState(
            measurementId = measurement.id,
            systolic = measurement.systolic.toString(),
            diastolic = measurement.diastolic.toString(),
            pulse = measurement.pulse?.toString().orEmpty(),
            measuredAtMillis = measurement.measuredAtMillis,
            note = measurement.note.orEmpty(),
            createdAtMillis = measurement.createdAtMillis,
            isEditMode = true,
        )
    }

    fun updateSystolic(value: String) = _uiState.update { it.copy(systolic = value, systolicError = null, saveStatus = SaveStatus.Idle) }
    fun updateDiastolic(value: String) = _uiState.update { it.copy(diastolic = value, diastolicError = null, saveStatus = SaveStatus.Idle) }
    fun updatePulse(value: String) = _uiState.update { it.copy(pulse = value, pulseError = null, saveStatus = SaveStatus.Idle) }
    fun updateMeasuredAt(millis: Long) = _uiState.update { it.copy(measuredAtMillis = millis, saveStatus = SaveStatus.Idle) }
    fun updateNote(value: String) = _uiState.update { it.copy(note = value, saveStatus = SaveStatus.Idle) }

    fun save() = viewModelScope.launch {
        val state = _uiState.value
        val validation = MeasurementValidator.validate(state.systolic, state.diastolic, state.pulse)
        if (!validation.isValid) {
            _uiState.update { it.copy(systolicError = validation.systolicError, diastolicError = validation.diastolicError, pulseError = validation.pulseError) }
            return@launch
        }
        val now = System.currentTimeMillis()
        repository.save(
            BloodPressureMeasurement(
                id = state.measurementId ?: 0,
                systolic = state.systolic.toInt(),
                diastolic = state.diastolic.toInt(),
                pulse = state.pulse.takeIf { it.isNotBlank() }?.toInt(),
                measuredAtMillis = state.measuredAtMillis,
                note = state.note.trim().ifBlank { null },
                createdAtMillis = if (state.isEditMode) state.createdAtMillis else now,
                updatedAtMillis = now,
            ),
        )
        _uiState.value = AddMeasurementUiState(saveStatus = SaveStatus.Saved)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CardioLogApplication
                val savedStateHandle = androidx.lifecycle.createSavedStateHandle(extras)
                @Suppress("UNCHECKED_CAST")
                return AddMeasurementViewModel(application.repository, savedStateHandle) as T
            }
        }
    }
}
