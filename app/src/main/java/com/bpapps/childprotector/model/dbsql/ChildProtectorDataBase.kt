package com.bpapps.childprotector.model.dbsql

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bpapps.childprotector.model.classes.Location
import com.bpapps.childprotector.model.classes.User

@Database(entities = [User::class, Location::class], version = 1, exportSchema = false)
@TypeConverters(ChildProtectorTypeConverters::class)
abstract class ChildProtectorDataBase : RoomDatabase() {

    abstract fun childProtectorDao(): ChildProtectorDao
}