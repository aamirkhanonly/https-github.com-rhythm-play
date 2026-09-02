package com.example.data.database

import androidx.room.TypeConverter
import com.example.model.AudioSourceType

class Converters {
    @TypeConverter
    fun fromAudioSourceType(value: AudioSourceType): String = value.name

    @TypeConverter
    fun toAudioSourceType(value: String): AudioSourceType =
        try { AudioSourceType.valueOf(value) } catch (e: Exception) { AudioSourceType.LOCAL_OFFLINE }

    @TypeConverter
    fun fromStringList(list: List<String>?): String = list?.joinToString(",") ?: ""

    @TypeConverter
    fun toStringList(data: String?): List<String> =
        if (data.isNullOrBlank()) emptyList() else data.split(",").filter { it.isNotBlank() }
}
