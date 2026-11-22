package com.amir.catfact.utils

import android.content.Context
import android.content.SharedPreferences
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object StreakManager {
    private const val PREF_NAME = "cat_fact_streak_prefs"
    private const val KEY_STREAK_COUNT = "key_streak_count"
    private const val KEY_LAST_READ_TIMESTAMP = "key_last_read_timestamp"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    data class StreakState(
        val count: Int,
        val isWarningState: Boolean,
        val isExtinguished: Boolean
    )

    fun getStreakState(context: Context): StreakState {
        val prefs = getPrefs(context)
        val count = prefs.getInt(KEY_STREAK_COUNT, 0)
        val lastTimestamp = prefs.getLong(KEY_LAST_READ_TIMESTAMP, 0L)

        if (lastTimestamp == 0L) {
            return StreakState(count = 0, isWarningState = false, isExtinguished = false)
        }

        val lastDate = Instant.ofEpochMilli(lastTimestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()

        val dayDiff = ChronoUnit.DAYS.between(lastDate, today)

        return when {
            dayDiff == 0L -> {
                // Read today, streak active & up to date
                StreakState(count = count, isWarningState = false, isExtinguished = false)
            }
            dayDiff == 1L -> {
                // Read yesterday, not read today yet -> streak active, but needs warning on widget
                StreakState(count = count, isWarningState = true, isExtinguished = false)
            }
            else -> {
                // Missed 2 or more days -> streak reset to 0
                if (count > 0) {
                    prefs.edit().putInt(KEY_STREAK_COUNT, 0).apply()
                }
                StreakState(count = 0, isWarningState = false, isExtinguished = true)
            }
        }
    }

    fun recordFactRead(context: Context): StreakState {
        val prefs = getPrefs(context)
        val currentCount = prefs.getInt(KEY_STREAK_COUNT, 0)
        val lastTimestamp = prefs.getLong(KEY_LAST_READ_TIMESTAMP, 0L)

        val today = LocalDate.now()
        var newCount = currentCount

        if (lastTimestamp != 0L) {
            val lastDate = Instant.ofEpochMilli(lastTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val dayDiff = ChronoUnit.DAYS.between(lastDate, today)

            when {
                dayDiff == 0L -> {
                    // Already read today, count stays same
                    newCount = if (currentCount == 0) 1 else currentCount
                }
                dayDiff == 1L -> {
                    // Consecutive day -> streak + 1
                    newCount = currentCount + 1
                }
                else -> {
                    // Missed 2+ days -> start new streak of 1
                    newCount = 1
                }
            }
        } else {
            // First time ever reading a fact -> streak = 1
            newCount = 1
        }

        prefs.edit()
            .putInt(KEY_STREAK_COUNT, newCount)
            .putLong(KEY_LAST_READ_TIMESTAMP, System.currentTimeMillis())
            .apply()

        return StreakState(count = newCount, isWarningState = false, isExtinguished = false)
    }
}