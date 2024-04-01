package com.blueskybone.screenshotdemo.util

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 *   Created by blueskybone
 *   Date: 2024/4/1
 */
data class WDHMS(
    val week: Long,
    val day: Long,
    val hour: Long,
    val min: Long,
    val sec: Long
)

object TimeUtils {
    private val calender = Calendar.getInstance()
    private val zoneId = ZoneId.of("Asia/Shanghai")

    //time to n day n hour n min
    private fun getMinusWDHMS(sec: Long): WDHMS {
        val w = sec / 604800
        val d = (sec % 604800) / 86400
        val h = (sec % 86400) / 3600
        val m = (sec % 3600) / 60
        val s = sec % 60
        return WDHMS(w, d, h, m, s)
    }

    private fun getWDHMSToStr(wdhms: WDHMS): String {
        val stringBuilder = StringBuilder()
        if (wdhms.week > 0) stringBuilder.append("${wdhms.week}周")
        if (wdhms.day > 0) stringBuilder.append("${wdhms.day}天")
        if (wdhms.hour > 0) stringBuilder.append("${wdhms.hour}时")
        if (wdhms.min > 0) stringBuilder.append("${wdhms.min}分")

        return stringBuilder.toString()
//        if (wdhms.sec > 0) stringBuilder.append("${wdhms.sec}秒")
    }

    fun getRemainTimeStr(sec: Long): String {
        return getWDHMSToStr(getMinusWDHMS(sec))
    }

    fun getWeekNum(ts: Long): Long {
        val tsc = ts + 100800000L
        val weekFields = WeekFields.of(Locale.CHINA)
        val weekNum = Instant.ofEpochMilli(tsc)
            .atZone(zoneId)
            .get(weekFields.weekOfWeekBasedYear())
        return weekNum.toLong()
    }

    fun getMonthNum(ts: Long): Long {
        val monthNum = Instant.ofEpochMilli(ts)
            .atZone(zoneId)
            .get(ChronoField.MONTH_OF_YEAR)
        return monthNum.toLong()
    }

    fun getDayNum(ts: Long): Long {
        //beijing: timestamp + 28800
        return (ts + 28800) / 86400
    }

    fun getDateTimeStr(): String {
        val date = Date()
        val df = DateFormat.getDateTimeInstance()
        return df.format(date)
    }

    fun getLoggerTimeStr(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        return "[$formatted]"
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeStr(ts: Long): String {
        val sdf = SimpleDateFormat("MM-dd HH:mm:ss")
        val date = Date(ts)
        return sdf.format(date)
    }

    fun getFormatDate(pattern: String = "yyyyMMdd_HH:mm:ss"): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return current.format(formatter)
    }
}