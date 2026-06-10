package com.cardiolog.app.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.cardiolog.app.CardioLogApplication
import com.cardiolog.app.data.BloodPressureRepository
import com.cardiolog.app.domain.BloodPressureMeasurement
import com.cardiolog.app.domain.ChartRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ChartsViewModel(private val repository: BloodPressureRepository) : ViewModel() {
    private val selectedRange = MutableStateFlow(ChartRange.Daily)
    private val selectedDay = MutableStateFlow(LocalDate.now())
    private val selectedWeek = MutableStateFlow(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ChartsUiState> = combine(selectedRange, selectedDay, selectedWeek, selectedMonth) { range, day, week, month ->
        RangeSelection(range, day, week, month)
    }.flatMapLatest { selection ->
        val (start, end) = selection.millisRange()
        repository.observeBetween(start, end).combine(selectedRange) { measurements, range ->
            ChartsUiState(range, selection.day, selection.weekStart, selection.month, measurements)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChartsUiState())

    fun setRange(range: ChartRange) = selectedRange.update { range }
    fun previous() {
        when (selectedRange.value) {
            ChartRange.Daily -> selectedDay.update { it.minusDays(1) }
            ChartRange.Weekly -> selectedWeek.update { it.minusWeeks(1) }
            ChartRange.Monthly -> selectedMonth.update { it.minusMonths(1) }
        }
    }
    fun next() {
        when (selectedRange.value) {
            ChartRange.Daily -> selectedDay.update { it.plusDays(1) }
            ChartRange.Weekly -> selectedWeek.update { it.plusWeeks(1) }
            ChartRange.Monthly -> selectedMonth.update { it.plusMonths(1) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CardioLogApplication
                @Suppress("UNCHECKED_CAST")
                return ChartsViewModel(application.repository) as T
            }
        }
    }
}

data class ChartsUiState(
    val range: ChartRange = ChartRange.Daily,
    val day: LocalDate = LocalDate.now(),
    val weekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
    val month: YearMonth = YearMonth.now(),
    val measurements: List<BloodPressureMeasurement> = emptyList(),
)

private data class RangeSelection(val range: ChartRange, val day: LocalDate, val weekStart: LocalDate, val month: YearMonth) {
    fun millisRange(): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val start = when (range) {
            ChartRange.Daily -> day.atStartOfDay(zone)
            ChartRange.Weekly -> weekStart.atStartOfDay(zone)
            ChartRange.Monthly -> month.atDay(1).atStartOfDay(zone)
        }
        val end = when (range) {
            ChartRange.Daily -> day.plusDays(1).atStartOfDay(zone).minusNanos(1)
            ChartRange.Weekly -> weekStart.plusWeeks(1).atStartOfDay(zone).minusNanos(1)
            ChartRange.Monthly -> month.plusMonths(1).atDay(1).atStartOfDay(zone).minusNanos(1)
        }
        return start.toInstant().toEpochMilli() to end.toInstant().toEpochMilli()
    }
}
