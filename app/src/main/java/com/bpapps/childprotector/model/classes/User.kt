package com.bpapps.childprotector.model.classes

import java.util.*
import kotlin.collections.ArrayList

class User(
    val _id: UUID,
    val _phoneNumber: String,
    val _name: String,
    val _userType: UserType
) {

    companion object {
        const val MAX_CONNECTED_USER = 8
    }

    private var _connectedUsersIds: ArrayList<UUID> = ArrayList()
        get() = this._connectedUsersIds


    fun addConnectedUser(userIdToAdd: UUID): Exception? {
        return when {
            _connectedUsersIds.size == MAX_CONNECTED_USER ->
                Exception("Max connected user allowed is $MAX_CONNECTED_USER")

            userIdToAdd == _id ->
                Exception("Can't add connected user with tje same id")

            else -> {
                _connectedUsersIds.add(userIdToAdd)
                return null
            }
        }
    }
}