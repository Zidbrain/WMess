package com.example.wmess.ui.formatters

import java.time.*
import java.time.format.*
import java.time.temporal.*

private val recentFormat = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
private val weekFormat = DateTimeFormatter.ofPattern("EEE").withZone(ZoneId.systemDefault())
private val oldFormat = DateTimeFormatter.ofPattern("MMM, dd").withZone(ZoneId.systemDefault())

fun formatInstant(instant: Instant): String {
    val now = Instant.now()
    val days = ChronoUnit.DAYS.between(instant, now)
    return if (days < 1)
        recentFormat.format(instant)
    else if (days < 7)
        weekFormat.format(instant)
    else
        oldFormat.format(instant)
}