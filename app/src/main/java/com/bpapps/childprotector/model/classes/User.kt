package com.bpapps.childprotector.model.classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bpapps.childprotector.model.ChildProtectorRepository

@Entity(tableName = ChildProtectorRepository.SQL_DB_TABLE_USERS_NAME)
data class User(
    @PrimaryKey val _id: String,
    val _phoneNumber: String,
    val _name: String,
    val _userType: @UserType Int,
    val _connectivityCode: String? = null
) {

    constructor() : this("", "", "", ErrorType.NOUN, null)

    companion object {
        const val MAX_CONNECTED_USER = 8
    }

    override fun toString(): String {
        return "User(_id='$_id', _phoneNumber='$_phoneNumber', _name='$_name', _userType=$_userType, _connectivityCode=$_connectivityCode)"
    }
}