package com.ngedev.newsapplicationcompose.utils

import java.text.SimpleDateFormat
import java.util.*

object TimestampConverter {
    fun String.toDate(
        dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss",
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Date? {

        val parser = SimpleDateFormat(dateFormat, Locale.ENGLISH)
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault(),): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
}