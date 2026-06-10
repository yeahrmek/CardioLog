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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardiolog.app.domain.BloodPressureMeasurement
import com.cardiolog.app.domain.ChartRange
import com.cardiolog.app.domain.MeasurementPeriod
import com.cardiolog.app.ui.components.formatDay
import com.cardiolog.app.ui.components.formatMonth
import com.cardiolog.app.ui.components.formatWeek
import com.cardiolog.app.ui.components.toLocalDateTime
import kotlin.math.ceil
import kotlin.math.floor
import java.time.temporal.ChronoUnit

@Composable
fun ChartsScreen(viewModel: ChartsViewModel = viewModel(factory = ChartsViewModel.Factory)) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Графики", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.range == ChartRange.Daily, onClick = { viewModel.setRange(ChartRange.Daily) }, label = { Text("День") })
            FilterChip(selected = state.range == ChartRange.Weekly, onClick = { viewModel.setRange(ChartRange.Weekly) }, label = { Text("Неделя") })
            FilterChip(selected = state.range == ChartRange.Monthly, onClick = { viewModel.setRange(ChartRange.Monthly) }, label = { Text("Месяц") })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = viewModel::previous) { Icon(Icons.Default.ChevronLeft, contentDescription = "Предыдущий период") }
            Text(
                text = when (state.range) {
                    ChartRange.Daily -> formatDay(state.day)
                    ChartRange.Weekly -> formatWeek(state.weekStart)
                    ChartRange.Monthly -> formatMonth(state.month)
                },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(onClick = viewModel::next) { Icon(Icons.Default.ChevronRight, contentDescription = "Следующий период") }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (state.measurements.isEmpty()) {
                    Text("Нет измерений за этот период", style = MaterialTheme.typography.titleMedium)
                    Text("Сохраненные измерения появятся здесь в виде спокойных линий тренда.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Legend()
                    BloodPressureChart(state.measurements, state.range, state.weekStart, Modifier.fillMaxWidth().height(280.dp))
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Сист.", color = Color(0xFFC64242), fontWeight = FontWeight.Bold)
            Text("Диаст.", color = Color(0xFF2F6FDB), fontWeight = FontWeight.Bold)
            Text("Пульс", color = Color(0xFF2E8B57), fontWeight = FontWeight.Bold)
        }
        Text("Период определяется автоматически по времени: ночь, утро, день, вечер.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BloodPressureChart(
    measurements: List<BloodPressureMeasurement>,
    range: ChartRange,
    weekStart: java.time.LocalDate,
    modifier: Modifier = Modifier,
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val systolicColor = Color(0xFFC64242)
    val diastolicColor = Color(0xFF2F6FDB)
    val pulseColor = Color(0xFF2E8B57)
    val periodInnerColor = MaterialTheme.colorScheme.surfaceContainerHighest
    Canvas(modifier = modifier) {
        val left = 50f
        val right = size.width - 50f
        val top = 18f
        val bottom = size.height - 42f
        val bpValues = measurements.flatMap { listOf(it.systolic, it.diastolic) }
        var bpMin = floor((bpValues.minOrNull()!! - 8) / 5f).toInt() * 5
        var bpMax = ceil((bpValues.maxOrNull()!! + 8) / 5f).toInt() * 5
        if (bpMax - bpMin < 25) {
            val center = (bpMax + bpMin) / 2
            bpMin = center - 15
            bpMax = center + 15
        }
        val pulseValues = measurements.mapNotNull { it.pulse }
        var pulseMin = pulseValues.minOrNull()?.let { floor((it - 6) / 5f).toInt() * 5 } ?: 40
        var pulseMax = pulseValues.maxOrNull()?.let { ceil((it + 6) / 5f).toInt() * 5 } ?: 120
        if (pulseMax - pulseMin < 20) {
            val center = (pulseMax + pulseMin) / 2
            pulseMin = center - 12
            pulseMax = center + 12
        }
        fun yBp(value: Int): Float = bottom - ((value - bpMin).toFloat() / (bpMax - bpMin).toFloat()) * (bottom - top)
        fun yPulse(value: Int): Float = bottom - ((value - pulseMin).toFloat() / (pulseMax - pulseMin).toFloat()) * (bottom - top)
        fun x(measurement: BloodPressureMeasurement, index: Int): Float = when (range) {
            ChartRange.Daily -> if (measurements.size == 1) (left + right) / 2 else left + (right - left) * index / (measurements.lastIndex.toFloat())
            ChartRange.Weekly -> {
                val dayOffset = ChronoUnit.DAYS.between(weekStart, measurement.measuredAtMillis.toLocalDateTime().toLocalDate())
                left + (right - left) * dayOffset.toFloat().coerceIn(0f, 6f) / 6f
            }
            ChartRange.Monthly -> {
                val day = measurement.measuredAtMillis.toLocalDateTime().dayOfMonth
                val maxDay = measurement.measuredAtMillis.toLocalDateTime().toLocalDate().lengthOfMonth()
                left + (right - left) * (day - 1) / (maxDay - 1).coerceAtLeast(1).toFloat()
            }
        }
        for (i in 0..4) {
            val yy = top + (bottom - top) * i / 4f
            drawLine(gridColor, Offset(left, yy), Offset(right, yy), strokeWidth = 1.5f)
            val bpLabel = bpMax - ((bpMax - bpMin) * i / 4f)
            val pulseLabel = pulseMax - ((pulseMax - pulseMin) * i / 4f)
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = labelColor.toArgb()
                    textSize = 24f
                    isAntiAlias = true
                }
                drawText(bpLabel.toInt().toString(), 2f, yy + 8f, paint)
                drawText(pulseLabel.toInt().toString(), right + 10f, yy + 8f, paint)
            }
        }
        drawSeries(measurements.mapIndexed { index, it -> Offset(x(it, index), yBp(it.systolic)) }, systolicColor)
        drawSeries(measurements.mapIndexed { index, it -> Offset(x(it, index), yBp(it.diastolic)) }, diastolicColor)
        val pulsePoints = measurements.mapIndexedNotNull { index, measurement ->
            measurement.pulse?.let { Offset(x(measurement, index), yPulse(it)) }
        }
        drawSeries(pulsePoints, pulseColor)
        measurements.forEachIndexed { index, measurement ->
            val markerRadius = when (measurement.period) {
                MeasurementPeriod.Night -> 7f
                MeasurementPeriod.Morning -> 5f
                MeasurementPeriod.Day -> 6f
                MeasurementPeriod.Evening -> 7f
            }
            val systolicPoint = Offset(x(measurement, index), yBp(measurement.systolic))
            val diastolicPoint = Offset(x(measurement, index), yBp(measurement.diastolic))
            drawCircle(systolicColor, markerRadius, systolicPoint)
            drawCircle(diastolicColor, markerRadius, diastolicPoint)
            if (measurement.period != MeasurementPeriod.Morning) {
                drawCircle(periodInnerColor, 3f, systolicPoint)
                drawCircle(periodInnerColor, 3f, diastolicPoint)
            }
            measurement.pulse?.let {
                val pulsePoint = Offset(x(measurement, index), yPulse(it))
                drawCircle(pulseColor, markerRadius, pulsePoint)
                if (measurement.period != MeasurementPeriod.Morning) {
                    drawCircle(periodInnerColor, 3f, pulsePoint)
                }
            }
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
