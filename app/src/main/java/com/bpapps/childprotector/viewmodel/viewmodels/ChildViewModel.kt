package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.User

class ChildViewModel : ViewModel(), ChildProtectorRepository.IOnRegisteredChildLoaded {

    private val repository = ChildProtectorRepository.getInstance()

    private lateinit var user: User

    init {
        repository.loadRegisteredChild(this)
    }

    private var childLoadedCallback: IChildLoaded? = null

    private var isMonitored: Boolean = false
    private var monitoringChangeStatusCallBack: IMonitoringStatusChanged? = null

    var monitoringStatus: Boolean = isMonitored
        get() = isMonitored

    fun changeMonitoringStatus(beingMonitored: Boolean) {
        isMonitored = beingMonitored
        monitoringChangeStatusCallBack?.monitored(beingMonitored)
    }

    override fun onLoaded(child: User) {
        user = child
        childLoadedCallback?.onLoaded(child)
    }

    fun registerMonitoringChangeStatus(callBack: IMonitoringStatusChanged) {
        monitoringChangeStatusCallBack = callBack
    }

    fun unRegisterMonitoringChangeStatus() {
        monitoringChangeStatusCallBack = null
    }

    fun registerForChildLoaded(callBack: IChildLoaded) {
        childLoadedCallback = callBack

        user?.let{
            childLoadedCallback?.onLoaded(user)
        }
    }

    fun unRegisterForChildLoaded() {
        childLoadedCallback = null
    }

//    fun getLocations(): List<Location> {
//        return _repository.getLocations()
//    }

    interface IMonitoringStatusChanged {
        fun monitored(isMonitored: Boolean)
    }

    interface IChildLoaded {
        fun onLoaded(child: User)
    }
}