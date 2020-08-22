package com.bpapps.childprotector.model

import com.bpapps.childprotector.model.classes.ConnectedUser
import com.bpapps.childprotector.model.classes.User
import java.util.*
import kotlin.collections.ArrayList

class ChildProtectorRepository private constructor() {
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
        private var repositoryInstance: ChildProtectorRepository? = null

        fun getInstance(): ChildProtectorRepository {
            if (repositoryInstance == null) {
                repositoryInstance = ChildProtectorRepository()
            }

            return repositoryInstance!!
        }
    }
}