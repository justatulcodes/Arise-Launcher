package com.expeknow.ariselauncher.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ModelTypeConverters {
    @TypeConverter
    fun fromTaskLinkList(value: List<TaskLink>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toTaskLinkList(value: String): List<TaskLink> {
        val listType = object : TypeToken<List<TaskLink>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDaysOfWeekList(value : List<DaysOfWeek>) : String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toDaysOfWeekList(value : String) : List<DaysOfWeek> {
        val listType = object : TypeToken<List<DaysOfWeek>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDriveItemType(type: DriveItemType): String {
        return type.name
    }

    @TypeConverter
    fun toDriveItemType(value: String): DriveItemType {
        return DriveItemType.valueOf(value)
    }

}