package com.cardiolog.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardiolog.app.domain.Sex

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Профиль", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Эти данные хранятся только на устройстве и помогают вести дневник давления лично для вас.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::updateName,
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.age,
            onValueChange = viewModel::updateAge,
            label = { Text("Возраст") },
            isError = state.ageError != null,
            supportingText = { state.ageError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Text("Пол", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Sex.entries.forEach { sex ->
                FilterChip(
                    selected = state.sex == sex,
                    onClick = { viewModel.updateSex(sex) },
                    label = { Text(sex.title) },
                )
            }
        }
        Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
            Text("Сохранить профиль")
        }
        if (state.saved) {
            Text("Профиль сохранен.", color = MaterialTheme.colorScheme.primary)
        }
    }
}

