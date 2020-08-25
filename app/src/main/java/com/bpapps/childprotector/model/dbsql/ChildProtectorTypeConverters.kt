package com.bpapps.childprotector.model.dbsql

import androidx.room.TypeConverter
import java.util.*

class ChildProtectorTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSecSinceEpoch: Long?): Date? {
        return millisSecSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid:UUID?) :String? {
        return uuid?.toString()
    }
}