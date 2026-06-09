package com.cardiolog.app.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardiolog.app.ui.components.formatDate
import com.cardiolog.app.ui.components.formatTime
import com.cardiolog.app.ui.components.toLocalDateTime
import com.cardiolog.app.ui.components.toMillis
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementScreen(
    onSaved: () -> Unit,
    viewModel: AddMeasurementViewModel = viewModel(factory = AddMeasurementViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val measuredAt = state.measuredAtMillis.toLocalDateTime()

    LaunchedEffect(state.saveStatus) {
        if (state.saveStatus is SaveStatus.Saved) onSaved()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (state.isEditMode) "Edit measurement" else "Add measurement",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Record blood pressure with the correct measurement date and time.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.systolic,
                    onValueChange = viewModel::updateSystolic,
                    label = { Text("Systolic") },
                    suffix = { Text("mmHg") },
                    isError = state.systolicError != null,
                    supportingText = { state.systolicError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = state.diastolic,
                    onValueChange = viewModel::updateDiastolic,
                    label = { Text("Diastolic") },
                    suffix = { Text("mmHg") },
                    isError = state.diastolicError != null,
                    supportingText = { state.diastolicError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                )
            }
            OutlinedTextField(
                value = state.pulse,
                onValueChange = viewModel::updatePulse,
                label = { Text("Pulse (optional)") },
                suffix = { Text("bpm") },
                isError = state.pulseError != null,
                supportingText = { state.pulseError?.let { Text(it) } ?: Text("Recommended when available") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Text("  ${formatDate(measuredAt.toLocalDate())}")
                }
                OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Text("  ${formatTime(measuredAt.toLocalTime())}")
                }
            }
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::updateNote,
                label = { Text("Note (optional)") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.isEditMode) "Save changes" else "Save measurement")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.measuredAtMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        val selectedDate = Instant.ofEpochMilli(utcMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                        viewModel.updateMeasuredAt(LocalDateTime.of(selectedDate, measuredAt.toLocalTime()).toMillis())
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = measuredAt.hour,
            initialMinute = measuredAt.minute,
            is24Hour = false,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    viewModel.updateMeasuredAt(LocalDateTime.of(measuredAt.toLocalDate(), newTime).toMillis())
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
            title = { Text("Measurement time") },
            text = { TimeInput(state = timePickerState) },
        )
    }
}
