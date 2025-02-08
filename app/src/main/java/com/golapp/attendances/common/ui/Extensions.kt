package com.golapp.attendances.common.ui

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

fun getMonthName(month: Int): String {
    val calendar: Calendar = GregorianCalendar()

    calendar.add(Calendar.MONTH, -month)

    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time).toString()

    return monthName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            java.util.Locale.ROOT
        ) else it.toString()
    }
}
