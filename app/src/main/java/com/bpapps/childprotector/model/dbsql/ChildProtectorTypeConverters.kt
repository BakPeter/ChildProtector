package com.bpapps.childprotector.model.dbsql

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.*

class ChildProtectorTypeConverters {

    @TypeConverter
    fun fromDate(date: Timestamp?): Long? {
        return date?.toDate()?.time
    }

    @TypeConverter
    fun toDate(millisSecSinceEpoch: Long?): Timestamp? {
        return millisSecSinceEpoch?.let {
            Timestamp(Date(it))
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