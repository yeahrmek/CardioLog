package com.cardiolog.app.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardiolog.app.domain.BloodPressureMeasurement
import com.cardiolog.app.ui.components.formatDateTime

@Composable
fun MeasurementListScreen(
    onEdit: (Long) -> Unit,
    viewModel: MeasurementListViewModel = viewModel(factory = MeasurementListViewModel.Factory),
) {
    val measurements by viewModel.measurements.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Измерения", style = MaterialTheme.typography.headlineMedium)
        if (measurements.isEmpty()) {
            EmptyListState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(measurements, key = { it.id }) { measurement ->
                    MeasurementRow(
                        measurement = measurement,
                        onEdit = { onEdit(measurement.id) },
                        onDelete = { viewModel.delete(measurement.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyListState() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Измерений пока нет", style = MaterialTheme.typography.titleMedium)
            Text("Добавьте первое измерение давления, и оно появится здесь.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MeasurementRow(measurement: BloodPressureMeasurement, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(formatDateTime(measurement.measuredAtMillis), style = MaterialTheme.typography.labelLarge)
                Text(
                    "${measurement.systolic}/${measurement.diastolic} мм рт. ст.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(measurement.period.title, style = MaterialTheme.typography.bodyMedium)
                measurement.pulse?.let { Text("Пульс $it уд/мин", style = MaterialTheme.typography.bodyMedium) }
                measurement.note?.let {
                    Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Редактировать измерение") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Удалить измерение") }
        }
    }
}
