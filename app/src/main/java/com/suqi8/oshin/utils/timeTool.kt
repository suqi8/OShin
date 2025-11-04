package com.suqi8.oshin.utils

import android.content.Context
import android.util.Log
import com.suqi8.oshin.R
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Locale

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: return dateString.substringBefore("T"))
    } catch (e: Exception) {
        dateString.substringBefore("T")
    }
}

fun formatTimeAgo(isoDateString: String, context: Context): String {
    return try {
        val instant = Instant.parse(isoDateString)
        val now = Instant.now()
        val duration = Duration.between(instant, now)
        val resources = context.resources

        when {
            duration.toDays() > 0 -> resources.getQuantityString(
                R.plurals.time_ago_days,
                duration.toDays().toInt(),
                duration.toDays().toInt()
            )
            duration.toHours() > 0 -> resources.getQuantityString(
                R.plurals.time_ago_hours,
                duration.toHours().toInt(),
                duration.toHours().toInt()
            )
            duration.toMinutes() > 0 -> resources.getQuantityString(
                R.plurals.time_ago_minutes,
                duration.toMinutes().toInt(),
                duration.toMinutes().toInt()
            )
            else -> context.getString(R.string.time_ago_just_now)
        }
    } catch (e: DateTimeParseException) {
        Log.e("FormatTimeAgo", "Failed to parse date: $isoDateString", e)
        ""
    } catch (e: Exception) {
        Log.e("FormatTimeAgo", "Error formatting time ago for: $isoDateString", e)
        ""
    }
}
