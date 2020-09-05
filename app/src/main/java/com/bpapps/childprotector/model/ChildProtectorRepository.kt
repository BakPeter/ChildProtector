package com.bpapps.childprotector.model

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.bpapps.childprotector.model.classes.ChildProtectorException
import com.bpapps.childprotector.model.classes.ChildToConnectInfo
import com.bpapps.childprotector.model.classes.Location
import com.bpapps.childprotector.model.classes.User
import com.bpapps.childprotector.model.dbsql.ChildProtectorDataBase
import com.bpapps.childprotector.model.dbwep.FireBaseDatabaseManager
import kotlinx.coroutines.channels.consumesAll
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
    private lateinit var locations: List<Location>

    init {
        executor.execute {
            locations = childProtectorDao.getLocations()
        }
    }

    private val fireBaseDatabaseManager: FireBaseDatabaseManager =
        FireBaseDatabaseManager.getInstance()

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
//            fireBaseDatabaseManager.loadUsersByConnectivityCodes(
//                parentsConnectivityCodes,
//                object : FireBaseDatabaseManager.IConnectedUsersLoadSuccess {
//                    override fun onSuccess(users: ArrayList<User>) {
//                        fireBaseDatabaseManager.loadUserByPhoneNumber(
//                            childPhoneNumber,
//                            object : FireBaseDatabaseManager.IUserLoadByPhoneNumberSuccess {
//                                override fun onSuccess(user: User) {
//                                    executor.execute {
//                                        childProtectorDao.addUser(user)
//                                        users.forEach { user ->
//                                            childProtectorDao.addUser(user)
//                                        }
//
//                                        registrationCallback?.onRegistrationSuccess(user, users)
//                                    }
//
//                                }
//                            },
//                            object : FireBaseDatabaseManager.IUserLoadByPhoneNumberFailure {
//                                override fun onFailure(error: ChildProtectorException) {
//                                    registrationCallback?.onRegistrationFailure(error)
//                                }
//                            }
//                        )
//                    }
//                },
//                object : FireBaseDatabaseManager.IConnectedUsersLoadFailure {
//                    override fun onFailure(error: ChildProtectorException) {
//                        registrationCallback?.onRegistrationFailure(error)
//                    }
//                })
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

    fun getRegisteredParent(callBack: IGotParent) {
        executor.execute {
            val parent = childProtectorDao.getRegisteredParent()
            callBack.onGot(parent)
        }
    }

    fun getRegisteredChildren(callBack: IGotChildren) {
        executor.execute {
            val children: List<User> = childProtectorDao.getRegisteredChildren()
            callBack.onGot(children)
        }
    }

    companion object {
        private const val TAG = "TAG.ChildProtectorRepository"

        private const val SQL_DATABASE_NAME = "childProtectorDataBase"
        const val SQL_DB_TABLE_LOCATIONS_NAME = "locations"
        const val SQL_DB_TABLE_USERS_NAME = "users"

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

    interface IGotParent {
        fun onGot(parent: User)
    }

    interface IGotChildren {
        fun onGot(children: List<User>)
    }
}