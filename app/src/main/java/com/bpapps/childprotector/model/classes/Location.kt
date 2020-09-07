package com.bpapps.childprotector.model.classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.google.firebase.Timestamp

@Entity(tableName = ChildProtectorRepository.SQL_DB_TABLE_LOCATIONS_NAME)
data class Location(
    @PrimaryKey val id: String,
    val userId: String,
    val date: Timestamp?,
    val longitude: Double?,
    val latitude: Double?
) {

    constructor() : this("", "", null, null, null)

    override fun toString(): String {
        return "Location(date=$date'\n'longitude=$longitude, latitude=$latitude\n'd=$id, userId=$userId)"
    }
}