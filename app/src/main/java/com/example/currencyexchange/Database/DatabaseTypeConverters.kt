package com.example.currencyexchange.Database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class DatabaseTypeConverters {


    @TypeConverter
    fun toMap(data: String): Map<String, Double>? {
        val type = object : TypeToken<Map<String, Double>?>() {}.type
        return Gson().fromJson(data, type)
    }

    @TypeConverter
    fun fromMap(map: Map<String, Double>?): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromTimestamp(date: Long?): Date? {
        return date?.let { Date(date) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time
    }
}