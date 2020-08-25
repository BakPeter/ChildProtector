package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.ConnectedUser
import com.bpapps.childprotector.model.classes.ErrorType
import com.bpapps.childprotector.model.classes.User

class ChildRegistrationViewModel : ViewModel() {

    private val _repository = ChildProtectorRepository.getInstance()

    private var _callBack: IParentConnectivityCodesUpdated? = null

    private var _user: User
        set(value) {
            _user = value
        }
        get() = _user

    private val _parentsConnectivityCodes: ArrayList<String> = arrayListOf()
    private var _childPhoneNumber: String? = null

    fun getParentsCodes(): ArrayList<String> = _parentsConnectivityCodes
    fun clearParentsCodes(callBack: IParentConnectivityCodesUpdated) {
        _parentsConnectivityCodes.clear()

        if (_callBack != null) callBack.codesUpdated(_parentsConnectivityCodes)
    }

    fun registerParentsCodeChangedCallBack(callBack: IParentConnectivityCodesUpdated) {
        _callBack = callBack
    }

    fun unRegisterParentsCodeChangedCallBack() {
        _callBack = null
    }

    fun addParentCode(code: String) {
        _parentsConnectivityCodes.add(code)
        _callBack?.codesUpdated(_parentsConnectivityCodes)
    }

    fun setChildPhoneNumber(childPhoneNumber: String?) {
        _childPhoneNumber = childPhoneNumber
    }

    fun registerChild(): ErrorType {
        checkInputValidation().let { errorType ->
            if (errorType.type != ErrorType.NOUN)
                return errorType
        }

        val connectedParents: ArrayList<ConnectedUser> =
            _repository!!.getConnectedUsers(_parentsConnectivityCodes)
        //TODO
//        checkIfParentsConnected()
//        at least one parent
//        if not return proper ErrorType

//        val childId = connectedParents[0]._childId
//        val user: User? = _repository.getUser(childId)
//
//        if (user == null) {
//            ErrorType(ErrorType.USER_NOT_IN_THE_DATA_BASE)
//        }

        return ErrorType(ErrorType.NOUN)
    }

    private fun checkInputValidation(): ErrorType {
        return when {
            _childPhoneNumber == null ->
                ErrorType(ErrorType.MISSING_USER_PHONE_NUMBER)

            _childPhoneNumber!!.isEmpty() ->
                ErrorType(ErrorType.MISSING_USER_PHONE_NUMBER)

            _parentsConnectivityCodes.size < 1 ->
                ErrorType(ErrorType.MISSING_AT_LEAST_ONE_PARENT_CODE)

            else ->
                ErrorType(ErrorType.NOUN)
        }
    }

    interface IParentConnectivityCodesUpdated {
        fun codesUpdated(codes: ArrayList<String>)
    }
}