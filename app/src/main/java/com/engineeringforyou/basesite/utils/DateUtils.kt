package com.engineeringforyou.basesite.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_PATTERN = "dd.MM.yyyy"
    private const val DATE_DAY_WEEK_PATTERN = "EE, dd.MM.yyyy"
    private const val DATE_HOUR_MINUTE_PATTERN = "HH:mm"
    private const val DATE_HOUR_MINUTE_DAY_PATTERN = "HH:mm dd.MM.yyyy"
    private const val EMPTY = "-"
    private const val MILLISECONDS_PER_SECOND = 1L

    fun parseDate(date: Long?) = if (date == null) EMPTY else format(date, DATE_PATTERN)

    fun parseDateDayWeek(date: Long?) = if (date == null) EMPTY else format(date, DATE_DAY_WEEK_PATTERN)

    fun parseDateDayWeekUTC(date: Long?) = if (date == null) EMPTY else formatUTC(date, DATE_DAY_WEEK_PATTERN)

    fun parseDateHourMinute(date: Long?) = if (date == null) EMPTY else format(date, DATE_HOUR_MINUTE_PATTERN)

    fun parseDateHourMinuteUTC(date: Long?) = if (date == null) EMPTY else formatUTC(date, DATE_HOUR_MINUTE_PATTERN)

    fun parseDateHourMinuteDay(date: Long?) = if (date == null) EMPTY else format(date, DATE_HOUR_MINUTE_DAY_PATTERN)

    fun parseDateHourMinuteDayUTC(date: Long?) = if (date == null) EMPTY else formatUTC(date, DATE_HOUR_MINUTE_DAY_PATTERN)

    private fun format(date: Long, pattern: String): String =
            SimpleDateFormat(pattern, Locale.getDefault()).format(date * MILLISECONDS_PER_SECOND).capitalize()

    private fun formatUTC(date: Long, pattern: String): String {
        val utcFormat = SimpleDateFormat(pattern, Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        return utcFormat.format(date * MILLISECONDS_PER_SECOND).capitalize()
    }

    fun getLocalTime(): Long {
        val calendar = Calendar.getInstance()
        val calendarLocal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendarLocal.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendarLocal.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        calendarLocal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        calendarLocal.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        calendarLocal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        calendarLocal.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND))
        return calendarLocal.time.time / MILLISECONDS_PER_SECOND
    }
}