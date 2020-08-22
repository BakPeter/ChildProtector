package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.User

class ChildViewModel : ViewModel() {

    val userName: String?
        get() {
//            return _user?._name
            return "Default Child name"
        }

    private val _repository = ChildProtectorRepository.getInstance()
    private var _user: User? = _repository.getCurrentUser()

    private var _isMonitored: Boolean = false
    private var _monitoringChangeStatusCallBack: IMonitoringStatusChanged? = null

    var monitoringStatus: Boolean = _isMonitored
        get() = _isMonitored

    fun changeMonitoringStatus(beingMonitored: Boolean) {
        _isMonitored = beingMonitored
        _monitoringChangeStatusCallBack?.monitored(beingMonitored)
    }


    fun registerMonitoringChangeStatus(callBack: IMonitoringStatusChanged) {
        _monitoringChangeStatusCallBack = callBack
    }
    fun unRegisterMonitoringChangeStatus() {
        _monitoringChangeStatusCallBack = null
    }

    interface IMonitoringStatusChanged {
        fun monitored(isMonitored: Boolean)
    }

}