package com.example.wasacon.slotapp

import android.util.Log
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DateAdministrator {
    companion object {
        @JvmStatic
        fun bindDay(date: Date?): Pair<Date, Date> {
            var localDate: LocalDate?
            val defaultZoneId = ZoneId.systemDefault()
            localDate = date?.toInstant()?.atZone(defaultZoneId)?.toLocalDate()
            Log.d("Print result: ", localDate.toString())
            val start = Date.from(localDate?.atStartOfDay(defaultZoneId)?.toInstant())
            val end = Date.from(localDate?.plusDays(1)?.atStartOfDay(defaultZoneId)?.toInstant())
            return Pair(start, end)
        }
    }
}