package com.callover.android.core.formatters

import java.time.Duration
import java.time.Instant
import java.util.Locale

object CallDurationFormatter {
    fun format(
        startedAt: Instant,
        now: Instant,
    ): String {
        val totalSeconds = Duration.between(startedAt, now)
            .seconds
            .coerceAtLeast(0)

        return format(totalSeconds)
    }

    fun format(totalSeconds: Long): String {
        val safeSeconds = totalSeconds.coerceAtLeast(0)

        val hours = safeSeconds / 3600
        val minutes = (safeSeconds % 3600) / 60
        val seconds = safeSeconds % 60

        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    }
}