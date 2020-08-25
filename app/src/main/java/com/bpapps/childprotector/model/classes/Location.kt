package com.bpapps.childprotector.model.classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bpapps.childprotector.model.ChildProtectorRepository
import java.util.*

@Entity(tableName = ChildProtectorRepository.DATA_BASE_TABLE_LOCATIONS_NAME)
data class Location(
    @PrimaryKey val id: UUID,
    val userId: UUID,
    val date: Date,
    val longitude: Double,
    val latitude: Double
) {
    override fun toString(): String {
        return "Location(date=$date'\n'longitude=$longitude, latitude=$latitude\n'd=$id, userId=$userId)"
    }
}