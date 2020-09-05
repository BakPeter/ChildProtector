package com.bpapps.childprotector.model.dbsql

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.Location
import com.bpapps.childprotector.model.classes.User
import com.bpapps.childprotector.model.classes.UserType

@Dao
interface ChildProtectorDao {

    @Query("SELECT * FROM " + ChildProtectorRepository.SQL_DB_TABLE_LOCATIONS_NAME)
    fun getLocations(): List<Location>

    @Insert
    fun addLocation(location: Location)

    @Insert
    fun addUser(user: User)

    @Query("SELECT * FROM " + ChildProtectorRepository.SQL_DB_TABLE_USERS + " WHERE _userType = " + UserType.PARENT + " LIMIT 1")
    fun getRegisteredParent(): User

    @Query("SELECT * FROM  ${ChildProtectorRepository.SQL_DB_TABLE_USERS} WHERE _userType = ${UserType.CHILD}")
    fun getRegisteredChildren(): List<User>

    @Query("SELECT * FROM ${ChildProtectorRepository.SQL_DB_TABLE_USERS} WHERE _userType = :userType")
    fun getRegisteredUser(userType: @UserType Int): User
}