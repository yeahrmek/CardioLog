package com.cardiolog.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.cardiolog.app.CardioLogApplication
import com.cardiolog.app.data.UserProfileRepository
import com.cardiolog.app.domain.Sex
import com.cardiolog.app.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val age: String = "",
    val sex: Sex = Sex.NotSpecified,
    val ageError: String? = null,
    val saved: Boolean = false,
)

class ProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeProfile().collect { profile ->
                _uiState.value = ProfileUiState(
                    name = profile.name,
                    age = profile.age?.toString().orEmpty(),
                    sex = profile.sex,
                )
            }
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value, saved = false) }
    fun updateAge(value: String) = _uiState.update { it.copy(age = value, ageError = null, saved = false) }
    fun updateSex(value: Sex) = _uiState.update { it.copy(sex = value, saved = false) }

    fun save() = viewModelScope.launch {
        val state = _uiState.value
        val age = state.age.takeIf { it.isNotBlank() }?.toIntOrNull()
        if (state.age.isNotBlank() && (age == null || age !in 1..120)) {
            _uiState.update { it.copy(ageError = "Возраст должен быть от 1 до 120.") }
            return@launch
        }
        repository.save(
            UserProfile(
                name = state.name.trim(),
                age = age,
                sex = state.sex,
            ),
        )
        _uiState.update { it.copy(saved = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CardioLogApplication
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(application.profileRepository) as T
            }
        }
    }
}

