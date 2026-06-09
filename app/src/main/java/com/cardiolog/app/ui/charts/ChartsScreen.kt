package com.cardiolog.app.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardiolog.app.domain.BloodPressureMeasurement
import com.cardiolog.app.domain.ChartRange
import com.cardiolog.app.ui.components.formatDay
import com.cardiolog.app.ui.components.formatMonth
import com.cardiolog.app.ui.components.toLocalDateTime

@Composable
fun ChartsScreen(viewModel: ChartsViewModel = viewModel(factory = ChartsViewModel.Factory)) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Charts", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.range == ChartRange.Daily, onClick = { viewModel.setRange(ChartRange.Daily) }, label = { Text("Daily") })
            FilterChip(selected = state.range == ChartRange.Monthly, onClick = { viewModel.setRange(ChartRange.Monthly) }, label = { Text("Monthly") })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = viewModel::previous) { Icon(Icons.Default.ChevronLeft, contentDescription = "Previous range") }
            Text(
                text = if (state.range == ChartRange.Daily) formatDay(state.day) else formatMonth(state.month),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(onClick = viewModel::next) { Icon(Icons.Default.ChevronRight, contentDescription = "Next range") }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (state.measurements.isEmpty()) {
                    Text("No readings in this range", style = MaterialTheme.typography.titleMedium)
                    Text("Saved measurements will appear as calm trend lines here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Legend()
                    BloodPressureChart(state.measurements, state.range, Modifier.fillMaxWidth().height(280.dp))
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Systolic", color = Color(0xFFC64242), fontWeight = FontWeight.Bold)
        Text("Diastolic", color = Color(0xFF2F6FDB), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BloodPressureChart(measurements: List<BloodPressureMeasurement>, range: ChartRange, modifier: Modifier = Modifier) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val systolicColor = Color(0xFFC64242)
    val diastolicColor = Color(0xFF2F6FDB)
    Canvas(modifier = modifier) {
        val left = 48f
        val right = size.width - 12f
        val top = 18f
        val bottom = size.height - 36f
        val minValue = ((measurements.minOf { it.diastolic } - 10).coerceAtMost(70) / 10) * 10
        val maxValue = (((measurements.maxOf { it.systolic } + 10).coerceAtLeast(160) + 9) / 10) * 10
        fun y(value: Int): Float = bottom - ((value - minValue).toFloat() / (maxValue - minValue).toFloat()) * (bottom - top)
        fun x(measurement: BloodPressureMeasurement, index: Int): Float = when (range) {
            ChartRange.Daily -> if (measurements.size == 1) (left + right) / 2 else left + (right - left) * index / (measurements.lastIndex.toFloat())
            ChartRange.Monthly -> {
                val day = measurement.measuredAtMillis.toLocalDateTime().dayOfMonth
                val maxDay = measurement.measuredAtMillis.toLocalDateTime().toLocalDate().lengthOfMonth()
                left + (right - left) * (day - 1) / (maxDay - 1).coerceAtLeast(1).toFloat()
            }
        }
        for (i in 0..4) {
            val yy = top + (bottom - top) * i / 4f
            drawLine(gridColor, Offset(left, yy), Offset(right, yy), strokeWidth = 1.5f)
        }
        drawSeries(measurements.mapIndexed { index, it -> Offset(x(it, index), y(it.systolic)) }, systolicColor)
        drawSeries(measurements.mapIndexed { index, it -> Offset(x(it, index), y(it.diastolic)) }, diastolicColor)
        measurements.forEachIndexed { index, measurement ->
            drawCircle(systolicColor, 5f, Offset(x(measurement, index), y(measurement.systolic)))
            drawCircle(diastolicColor, 5f, Offset(x(measurement, index), y(measurement.diastolic)))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSeries(points: List<Offset>, color: Color) {
    if (points.size == 1) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(path, color, style = Stroke(width = 5f, cap = StrokeCap.Round))
}
