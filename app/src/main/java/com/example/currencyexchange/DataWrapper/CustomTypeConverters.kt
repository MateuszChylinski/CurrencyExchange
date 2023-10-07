package com.example.currencyexchange.DataWrapper

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CustomTypeConverters {
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
}