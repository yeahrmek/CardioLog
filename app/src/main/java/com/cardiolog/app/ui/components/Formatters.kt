package com.cardiolog.app.ui.components

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val russianLocale = Locale("ru")
private val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", russianLocale)
private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", russianLocale)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", russianLocale)
private val dayFormatter = DateTimeFormatter.ofPattern("d MMM", russianLocale)
private val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", russianLocale)
private val weekFormatter = DateTimeFormatter.ofPattern("d MMM", russianLocale)

fun Long.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
fun LocalDateTime.toMillis(): Long = atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun formatDateTime(millis: Long): String = millis.toLocalDateTime().format(dateTimeFormatter)
fun formatDate(date: LocalDate): String = date.format(dateFormatter)
fun formatTime(time: LocalTime): String = time.format(timeFormatter)
fun formatDay(date: LocalDate): String = date.format(dayFormatter)
fun formatMonth(month: YearMonth): String = month.format(monthFormatter)
fun formatWeek(startDate: LocalDate): String =
    "${startDate.format(weekFormatter)} - ${startDate.plusDays(6).format(weekFormatter)}"

