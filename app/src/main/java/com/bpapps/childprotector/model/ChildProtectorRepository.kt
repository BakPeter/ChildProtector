package com.bpapps.childprotector.model

import android.content.Context
import androidx.room.Room
import com.bpapps.childprotector.model.classes.ConnectedUser
import com.bpapps.childprotector.model.classes.Location
import com.bpapps.childprotector.model.classes.User
import com.bpapps.childprotector.model.dbsql.ChildProtectorDataBase
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class ChildProtectorRepository private constructor(private val context: Context) {
    private val database: ChildProtectorDataBase =
        Room.databaseBuilder(
            context.applicationContext,
            ChildProtectorDataBase::class.java,
            DATABASE_NAME
        ).build()
    private val childProtectorDao = database.childProtectorDao()
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private lateinit var locations: List<Location>

    init {
        executor.execute {
            locations = childProtectorDao.getLocations()
        }
    }


    //============================================================================================
    //============================================================================================
    //locations
    fun getLocations(): List<Location> = locations

    fun addLocation(time: Date, userId: UUID, longitude: Double, latitude: Double) {
        executor.execute {
            val location = Location(UUID.randomUUID(), userId, time, longitude, latitude)
            childProtectorDao.addLocation(location)
        }
    }
    //============================================================================================
    //============================================================================================

    fun getConnectedUsers(parentsConnectivityCodes: ArrayList<String>): ArrayList<ConnectedUser> {
        //TODO getConnectedUsers
        return ArrayList<ConnectedUser>()
    }

    fun getUser(childId: UUID): User? {
        //TODO getUser
        //get user from server
        return null
    }

    fun getCurrentUser(): User? {
        //TODO getCurrentUser()

        return null
    }


    companion object {
        private const val DATABASE_NAME = "childProtectorDataBase"
        const val DATA_BASE_TABLE_LOCATIONS_NAME = "locations"

        private var repositoryInstance: ChildProtectorRepository? = null

        fun initialize(context: Context): ChildProtectorRepository {
            if (repositoryInstance == null) {
                repositoryInstance = ChildProtectorRepository(context)
            }

            return repositoryInstance!!
        }


        fun getInstance(): ChildProtectorRepository {
            if (repositoryInstance == null)
                throw IllegalStateException("Child protector repository must be initialized")
            return repositoryInstance!!
        }
    }
}