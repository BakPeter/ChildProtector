package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.User

class ParentViewViewModel : ViewModel() {
    private val repository: ChildProtectorRepository = ChildProtectorRepository.getInstance()

    private var userLoadedCallback: IUsersLoaded? = null

    var parent: User? = null
    var children: List<User>? = null

    init {
        repository.getRegisteredParent(object : ChildProtectorRepository.IGotParent {
            override fun onGot(parent: User) {
                this@ParentViewViewModel.parent = parent
            }
        })
        repository.getRegisteredChildren(object : ChildProtectorRepository.IGotChildren {
            override fun onGot(children: List<User>) {
                this@ParentViewViewModel.children = children

                userLoadedCallback?.onLoaded()
            }

        })
    }

    fun registerForUserLoadedCallBack(callBak:IUsersLoaded) {
        userLoadedCallback = callBak
    }
    fun unRegisterForUserLoadedCallBack() {
        userLoadedCallback = null
    }
    interface IUsersLoaded {
        fun onLoaded()
    }
}