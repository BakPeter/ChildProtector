package com.bpapps.childprotector.viewmodel

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository


class ChildProtectorViewModel : ViewModel() {

    private val repository = ChildProtectorRepository.getInstance()
    private val user = repository.getUser()
    private var counter = 0


    fun getUserName(): String {
        return user.name
    }

    fun getCounter(): String = counter.toString()

    fun upgradeCounter() {
        counter++
    }

    fun getTVUserNameText(): String =
        if (counter == 0)
            getUserName()
        else
            getUserName() + " : " + getCounter()
}