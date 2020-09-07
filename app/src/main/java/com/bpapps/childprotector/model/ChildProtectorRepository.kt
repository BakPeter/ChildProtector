package com.bpapps.childprotector.model

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.bpapps.childprotector.model.classes.*
import com.bpapps.childprotector.model.dbsql.ChildProtectorDataBase
import com.bpapps.childprotector.model.dbwep.FireBaseDatabaseManager
import com.google.firebase.Timestamp
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ChildProtectorRepository private constructor(private val context: Context) {
    private val sqlDatabase: ChildProtectorDataBase =
        Room.databaseBuilder(
            context.applicationContext,
            ChildProtectorDataBase::class.java,
            SQL_DATABASE_NAME
        ).build()
    private val childProtectorDao = sqlDatabase.childProtectorDao()
    private val executor: Executor = Executors.newSingleThreadExecutor()


    private val fireBaseDatabaseManager: FireBaseDatabaseManager =
        FireBaseDatabaseManager.getInstance()

    //============================================================================================
    //============================================================================================
    //locations
    fun loadLocations(
        childrenIds: ArrayList<String>,
        successCallback: IOnLocationsLoadedSuccesses?,
        failureCallback: IOnLocationLoadedFailure
    ) {
        executor.execute {
            fireBaseDatabaseManager.loadLocations(
                childrenIds,
                object : FireBaseDatabaseManager.ILocationsLoadedSuccess {
                    override fun onLoaded(locations: ArrayList<Location>) {
                        successCallback?.onLoaded(locations)
                    }
                },
                object : FireBaseDatabaseManager.ILocationsLoadedFailure {
                    override fun onFailure(error: ChildProtectorException) {
                        failureCallback?.onFailure(error)
                    }
                })
        }
    }

    fun addLocation(userId: String, time: Timestamp, longitude: Double, latitude: Double) {
        executor.execute {
            fireBaseDatabaseManager.addLocation(userId, time, longitude, latitude)
//            childProtectorDao.addLocation(location)
        }
    }
    //============================================================================================
    //============================================================================================


    //============================================================================================
    //============================================================================================
    //UsageStats

    fun loadUsageStats(
        childrenIds: ArrayList<String>,
        successCallback: IOnUsageStatsLoadedSuccesses?,
        failureCallback: IOnUsageStatsLoadedFailure?
    ) {
        executor.execute {
            fireBaseDatabaseManager.loadUsageStats(
                childrenIds,
                object : FireBaseDatabaseManager.IUsageStatsLoadSuccess {
                    override fun onLoaded(usageStats: ArrayList<AppUsageStatInterval>) {
                        successCallback?.onLoaded(usageStats)
                    }
                },
                object : FireBaseDatabaseManager.IUsageStatsFailure {
                    override fun onFailure(error: ChildProtectorException) {
                        failureCallback?.onFailure(error)
                    }
                }
            )
        }
    }

    fun addAppUsageStats(
        userId: String,
        appName: String,
        appCreator: String,
        appUsageTime: Double,
        updateInterval: Int
    ) {
        executor.execute {
            fireBaseDatabaseManager.addAppUsageStats(
                userId,
                appName,
                appCreator,
                appUsageTime,
                updateInterval
            )
        }
    }

    //============================================================================================
    //============================================================================================
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    fun registerChild(
        parentsConnectivityCodes: ArrayList<String>,
        childPhoneNumber: String,
        registrationCallback: IChildRegistrationFinished?
    ) {
        executor.execute {
            fireBaseDatabaseManager.loadUserByPhoneNumber(
                childPhoneNumber,
                object : FireBaseDatabaseManager.IUserLoadByPhoneNumberSuccess {
                    override fun onSuccess(child: User) {
                        Log.d(TAG, child.toString())
                        fireBaseDatabaseManager.loadUsersByConnectivityCodes(
                            parentsConnectivityCodes,
                            object : FireBaseDatabaseManager.IUsersLoadByConnectivityCodesSuccess {
                                override fun onSuccess(parents: ArrayList<User>) {

                                    executor.execute {
                                        childProtectorDao.addUser(child)
                                        parents.forEach { parent ->
                                            childProtectorDao.addUser(parent)
                                        }
                                    }

                                    registrationCallback?.onRegistrationSuccess(child, parents)
                                }
                            },
                            object : FireBaseDatabaseManager.IUsersLoadByConnectivityCodesFailure {
                                override fun onFailure(error: ChildProtectorException) {
                                    registrationCallback?.onRegistrationFailure(error)
                                }

                            })
                    }
                },
                object : FireBaseDatabaseManager.IUserLoadByPhoneNumberFailure {
                    override fun onFailure(error: ChildProtectorException) {
                        registrationCallback?.onRegistrationFailure(error)
                    }
                })
        }
    }

    fun registerParent(
        parentName: String,
        parentPhoneNumber: String,
        childrenToConnectInfo: ArrayList<ChildToConnectInfo>,
        parentCallBack: IParentRegistered?,
        childCallBack: IChildRegistered?
    ) {
        executor.execute {
            val parent: User =
                fireBaseDatabaseManager.addParent(
                    parentName,
                    parentPhoneNumber,
                    object :
                        FireBaseDatabaseManager.IParentRegistered {
                        override fun onRegistered(parent: User) {
                            parentCallBack?.onParentRegistered(parent)

                            executor.execute {
                                childProtectorDao.addUser(parent)
                            }
                        }
                    }
                )

            childrenToConnectInfo.forEachIndexed { index, childToConnectInfo ->
                fireBaseDatabaseManager.addChild(
                    childToConnectInfo.name,
                    childToConnectInfo.phoneNumber,
                    parent._id,
                    parent._connectivityCode!!,
                    object :
                        FireBaseDatabaseManager.IChildRegistered {
                        override fun onRegistered(child: User) {
                            childCallBack?.onChildRegistered(child, index + 1)

                            executor.execute {
                                childProtectorDao.addUser(child)
                            }
                        }
                    }
                )
            }
        }
    }

    fun loadRegisteredChild(callBack: IOnRegisteredChildLoaded?) {
        executor.execute {
            callBack?.let { it ->
                it.onLoaded(childProtectorDao.getRegisteredUser(UserType.CHILD))
            }
        }
    }

    fun loadRegisteredParent(callBack: IOnRegisteredParentLoaded?) {
        executor.execute {
            callBack?.let { it ->
                it.onLoaded(childProtectorDao.getRegisteredUser(UserType.PARENT))
            }
        }
    }

    fun loadChildren(callback: IOnRegisteredChildrenLoaded?) {
        executor.execute {
            callback?.let { it ->
                it.onLoaded(childProtectorDao.getRegisteredChildren())
            }
        }
    }

    companion object {
        private const val TAG = "TAG.ChildProtectorRepository"

        private const val SQL_DATABASE_NAME = "childProtectorDataBase"
        const val SQL_DB_TABLE_LOCATIONS_NAME = "locations"
        const val SQL_DB_TABLE_USERS = "users"

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

    interface IParentRegistered {
        fun onParentRegistered(parent: User?)
    }

    interface IChildRegistered {
        fun onChildRegistered(child: User?, childNumber: Int?)
    }

    interface IChildRegistrationFinished {
        fun onRegistrationSuccess(child: User, parents: ArrayList<User>)
        fun onRegistrationFailure(error: ChildProtectorException)
    }

    interface IOnRegisteredChildLoaded {
        fun onLoaded(child: User)
    }

    interface IOnRegisteredParentLoaded {
        fun onLoaded(parent: User)
    }

    interface IOnRegisteredChildrenLoaded {
        fun onLoaded(children: List<User>)
    }

    interface IOnLocationsLoadedSuccesses {
        fun onLoaded(locations: ArrayList<Location>)
    }

    interface IOnLocationLoadedFailure {
        fun onFailure(error: ChildProtectorException)
    }

    interface IOnUsageStatsLoadedSuccesses {
        fun onLoaded(usageStats: ArrayList<AppUsageStatInterval>)
    }

    interface IOnUsageStatsLoadedFailure {
        fun onFailure(error: ChildProtectorException)
    }
}