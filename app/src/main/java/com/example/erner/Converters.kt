package com.example.erner

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromPair(value: Pair<Int, Int>?): String? {
        return value?.let { "${it.first},${it.second}" }
    }

    @TypeConverter
    fun toPair(value: String?): Pair<Int, Int>? {
        return value?.split(",")?.let {
            if (it.size == 2) Pair(it[0].toInt(), it[1].toInt()) else null
        }
    }
}
