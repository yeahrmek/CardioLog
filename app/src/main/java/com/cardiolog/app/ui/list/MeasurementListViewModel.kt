package com.cardiolog.app.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.cardiolog.app.CardioLogApplication
import com.cardiolog.app.data.BloodPressureRepository
import com.cardiolog.app.domain.BloodPressureMeasurement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MeasurementListViewModel(private val repository: BloodPressureRepository) : ViewModel() {
    val measurements: StateFlow<List<BloodPressureMeasurement>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(id: Long) = viewModelScope.launch { repository.delete(id) }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CardioLogApplication
                @Suppress("UNCHECKED_CAST")
                return MeasurementListViewModel(application.repository) as T
            }
        }
    }
}
