package com.bpapps.childprotector.model.dbsql

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.Location

@Dao
interface ChildProtectorDao {

    @Query("SELECT * FROM " + ChildProtectorRepository.DATA_BASE_TABLE_LOCATIONS_NAME)
    fun getLocations(): List<Location>

    @Insert
    fun addLocation(location: Location)
}