package com.bpapps.childprotector.model.dbwep

import android.util.Log
import com.bpapps.childprotector.model.classes.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FireBaseDatabaseManager private constructor() {
    private var db: FirebaseFirestore = Firebase.firestore

    fun addParent(
        parentName: String,
        parentPhoneNumber: String,
        callBack: IParentRegistered?
    ): User {
        val parentRef = db.collection(COLLECTION_USERS).document()

        val newConnectivityCodeRef: DocumentReference =
            db.collection(COLLECTION_CONNECTIVITY_CODES).document()
        newConnectivityCodeRef.set(hashMapOf(FIELD_USER_ID to parentRef.id))

        val newParent =
            User(
                parentRef.id,
                parentPhoneNumber,
                parentName,
                UserType.PARENT,
                newConnectivityCodeRef.id
            )

        parentRef.set(newParent).addOnSuccessListener {
            callBack?.onRegistered(newParent)
        }

        return newParent
    }

    fun addChild(
        name: String,
        phoneNumber: String,
        parentId: String,
        parentConnectivityCode: String,
        callBack: IChildRegistered?
    ) {
        db.collection(COLLECTION_USERS)
            .whereEqualTo(FIELD_USER_PHONE_NUMBER, phoneNumber)
            .whereEqualTo(FIELD_USER_NAME, name)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val childRef = db.collection(COLLECTION_USERS).document()
                    val newChild = User(childRef.id, phoneNumber, name, UserType.CHILD)

                    addConnectedUsers(parentConnectivityCode, parentId, newChild._id, null, null)
//                    db.collection(COLLECTION_CONNECTED_USER).document().set(
//                        hashMapOf(
//                            FIELD_PARENT_ID to parentId,
//                            FIELD_CHILD_ID to newChild._id,
//                            FIELD_CONNECTIVITY_CODE to parentConnectivityCode
//                        )
//                    )

                    childRef.set(newChild).addOnSuccessListener {
                        callBack?.onRegistered(newChild)
                    }
                } else {
                    for (document in documents) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
                        addConnectedUsers(
                            parentConnectivityCode,
                            parentId,
                            document.id,
                            object : IConnectedUsersAddingSuccess {
                                override fun onAdded(connectedUsers: ConnectedUsers?) {
                                    callBack?.onRegistered(document.toObject<User>())
                                }
                            },
                            object : IConnectedUsersAddingFailure {
                                override fun onFailed(error: ChildProtectorException) {
                                }
                            }
                        )


//                        db.collection(COLLECTION_CONNECTED_USER).document().set(
//                            hashMapOf(
//                                FIELD_PARENT_ID to parentId,
//                                FIELD_CHILD_ID to document.id,
//                                FIELD_CONNECTIVITY_CODE to parentConnectivityCode
//                            )
//                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                throw exception
            }
    }

    private fun addConnectedUsers(
        connectivityCode: String,
        parentId: String,
        childId: String,
        successCallBack: IConnectedUsersAddingSuccess?,
        failureCallback: IConnectedUsersAddingFailure?
    ) {
        db.collection(COLLECTION_CONNECTED_USER).document()
            .set(
                hashMapOf(
                    FIELD_PARENT_ID to parentId,
                    FIELD_CHILD_ID to childId,
                    FIELD_CONNECTIVITY_CODE to connectivityCode
                )
            )
            .addOnSuccessListener {
                successCallBack?.onAdded(null)
            }
            .addOnFailureListener {
                failureCallback?.onFailed(
                    ChildProtectorException(
                        ErrorType.EXTERNAL_ERROR,
                        "",
                        null
                    )
                )
            }
    }

    fun loadUsersByConnectivityCodes(
        connectivityCodes: ArrayList<String>,
        successCallback: IUsersLoadByConnectivityCodesSuccess?,
        failureCallback: IUsersLoadByConnectivityCodesFailure?
    ) {

        db.collection(COLLECTION_USERS)
            .whereIn(FIELD_CONNECTIVITY_CODE, connectivityCodes)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val documents = querySnapshot.documents
                if (documents.size == 0) {
                    failureCallback?.onFailure(
                        ChildProtectorException(
                            ErrorType.USER_NOT_IN_THE_DATA_BASE,
                            "",
                            null
                        )
                    )

                    return@addOnSuccessListener
                }

                val users: ArrayList<User> = arrayListOf()
                for (document in documents) {
                    val user = document.toObject<User>()
                    Log.d(TAG, user.toString())
                    user?.let {
                        users.add(user)
                    }
                }

                successCallback?.onSuccess(users)
            }
            .addOnFailureListener { exception ->
                failureCallback?.onFailure(
                    ChildProtectorException(
                        ErrorType.EXTERNAL_ERROR,
                        exception.message,
                        exception
                    )
                )
            }
    }

    fun loadUserByPhoneNumber(
        childPhoneNumber: String,
        successCallback: IUserLoadByPhoneNumberSuccess?,
        failureCallback: IUserLoadByPhoneNumberFailure?
    ) {
        db.collection(COLLECTION_USERS).whereEqualTo(FIELD_USER_PHONE_NUMBER, childPhoneNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.size == 0) {
                    failureCallback?.onFailure(
                        ChildProtectorException(
                            ErrorType.PHONE_NOT_IN_THE_DATA_BASE,
                            "",
                            null
                        )
                    )
                } else {
                    val user = querySnapshot.documents[0].toObject<User>()
                    successCallback?.onSuccess(user!!)
                }
            }
            .addOnFailureListener { exception ->
                failureCallback?.onFailure(
                    ChildProtectorException(
                        ErrorType.EXTERNAL_ERROR,
                        exception.message,
                        exception
                    )
                )
            }
    }

    fun loadLocations(
        childrenIds: ArrayList<String>,
        successCallback: ILocationsLoadedSuccess?,
        failureCallback: ILocationsLoadedFailure?
    ) {
        val locRef = db.collection(COLLECTION_LOCATIONS)
        childrenIds.forEach { childId ->
            locRef.whereEqualTo(FIELD_USER_ID, childId)
        }

        locRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.size == 0) {
                    failureCallback?.onFailure(
                        ChildProtectorException(
                            ErrorType.USER_NOT_IN_THE_DATA_BASE,
                            "",
                            null
                        )
                    )
                } else {
                    val locations = arrayListOf<Location>()
                    for (document in querySnapshot) {
                        locations.add(document.toObject<Location>())
                    }

                    successCallback?.onLoaded(locations)
                }
            }
            .addOnFailureListener { exception ->
                failureCallback?.onFailure(
                    ChildProtectorException(
                        ErrorType.EXTERNAL_ERROR,
                        "",
                        exception
                    )
                )
            }
    }

    fun loadUsageStats(
        childrenIds: ArrayList<String>,
        successCallback: IUsageStatsLoadSuccess?,
        failureCallback: IUsageStatsFailure?
    ) {
        val locRef = db.collection(COLLECTION_USAGE_STATS)

        childrenIds.forEach { id ->
            locRef.whereEqualTo(FIELD_USER_ID, id)
        }

        locRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.size == 0) {
                    failureCallback?.onFailure(
                        ChildProtectorException(
                            ErrorType.USER_NOT_IN_THE_DATA_BASE,
                            "",
                            null
                        )
                    )
                } else {
                    val usageStats = arrayListOf<AppUsageStatInterval>()

                    for (document in querySnapshot) {
                        usageStats.add(document.toObject<AppUsageStatInterval>())
                    }

                    successCallback?.onLoaded(usageStats)
                }
            }
            .addOnFailureListener { error ->
                failureCallback?.onFailure(
                    ChildProtectorException(
                        ErrorType.EXTERNAL_ERROR,
                        "",
                        error
                    )
                )
            }
    }

    fun addLocation(userId: String, date: Timestamp, longitude: Double, latitude: Double) {
        val locRef = db.collection(COLLECTION_LOCATIONS).document()
        val location = Location(locRef.id, userId, date, longitude, latitude)

        locRef.set(location)
    }

    fun addAppUsageStats(
        userId: String,
        appName: String,
        appCreator: String,
        appUsageTime: Double,
        updateInterval: Int
    ) {
        val usRef = db.collection(COLLECTION_USAGE_STATS).document()
        val appUsageStatInterval = AppUsageStatInterval(
            usRef.id,
            userId,
            appName,
            appCreator,
            appUsageTime,
            updateInterval
        )

        usRef.set(appUsageStatInterval)
    }


    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_CONNECTED_USER = "connected users"
        private const val COLLECTION_CONNECTIVITY_CODES = "connectivity codes"
        private const val COLLECTION_LOCATIONS = "locations"
        private const val COLLECTION_USAGE_STATS = "usage stats"

        private const val FIELD_USER_ID = "_userId"
        private const val FIELD_USER_PHONE_NUMBER = "_phoneNumber"
        private const val FIELD_USER_NAME = "_name"
        private const val FIELD_PARENT_ID = "_parentId"
        private const val FIELD_CHILD_ID = "_childId"
        private const val FIELD_CONNECTIVITY_CODE = "_connectivityCode"

        private const val TAG = "TAG.FireBaseDatabaseManager"

        private var instance: FireBaseDatabaseManager? = null

        fun getInstance(): FireBaseDatabaseManager {
            if (instance == null)
                instance = FireBaseDatabaseManager()

            return instance!!
        }
    }

    interface IParentRegistered {
        fun onRegistered(parent: User)
    }

    interface IChildRegistered {
        fun onRegistered(child: User)
    }

    interface IUsersLoadByConnectivityCodesSuccess {
        fun onSuccess(users: ArrayList<User>)
    }

    interface IUsersLoadByConnectivityCodesFailure {
        fun onFailure(error: ChildProtectorException)
    }

    interface IUserLoadByPhoneNumberSuccess {
        fun onSuccess(user: User)
    }

    interface IUserLoadByPhoneNumberFailure {
        fun onFailure(error: ChildProtectorException)
    }

    interface IConnectedUsersAddingSuccess {
        fun onAdded(connectedUsers: ConnectedUsers?)
    }

    interface IConnectedUsersAddingFailure {
        fun onFailed(error: ChildProtectorException)
    }

    interface ILocationsLoadedSuccess {
        fun onLoaded(locations: ArrayList<Location>)
    }

    interface ILocationsLoadedFailure {
        fun onFailure(error: ChildProtectorException)
    }

    interface IUsageStatsLoadSuccess {
        fun onLoaded(usageStats: ArrayList<AppUsageStatInterval>)
    }

    interface IUsageStatsFailure {
        fun onFailure(error: ChildProtectorException)
    }
}