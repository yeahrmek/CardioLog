package com.cardiolog.app.ui.components

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a")
private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
private val dayFormatter = DateTimeFormatter.ofPattern("MMM d")
private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

fun Long.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
fun LocalDateTime.toMillis(): Long = atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
fun formatDateTime(millis: Long): String = millis.toLocalDateTime().format(dateTimeFormatter)
fun formatDate(date: LocalDate): String = date.format(dateFormatter)
fun formatTime(time: LocalTime): String = time.format(timeFormatter)
fun formatDay(date: LocalDate): String = date.format(dayFormatter)
fun formatMonth(month: YearMonth): String = month.format(monthFormatter)
