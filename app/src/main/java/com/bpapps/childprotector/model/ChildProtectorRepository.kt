package com.bpapps.childprotector.model

import java.util.*

class ChildProtectorRepository private constructor() {

    fun getUser() = User(UUID.randomUUID(), "Peter")

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