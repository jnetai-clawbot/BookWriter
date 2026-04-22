package com.jnetai.bookwriter.data.converter

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateStr: String?): LocalDate? = dateStr?.let { LocalDate.parse(it) }
}