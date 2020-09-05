package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.ChildProtectorException
import com.bpapps.childprotector.model.classes.ErrorType
import com.bpapps.childprotector.model.classes.User

class ChildRegistrationViewModel : ViewModel() {

    //For DEBUG :
    //parent connectivity code = 4AziVsFc0Vj0yXov7pdm
    val debugConnectivityCode = "4AziVsFc0Vj0yXov7pdm"

    private val repository = ChildProtectorRepository.getInstance()
    private var callBack: IParentConnectivityCodesUpdated? = null

    private val parentsConnectivityCodes: ArrayList<String> = arrayListOf()
    private var childPhoneNumber: String? = null

    fun getParentsCodes(): ArrayList<String> = parentsConnectivityCodes
    fun clearParentsCodes(callBack: IParentConnectivityCodesUpdated) {
        parentsConnectivityCodes.clear()

        if (this.callBack != null) callBack.codesUpdated(parentsConnectivityCodes)
    }

    fun registerParentsCodeChangedCallBack(callBack: IParentConnectivityCodesUpdated) {
        this.callBack = callBack
    }

    fun unRegisterParentsCodeChangedCallBack() {
        callBack = null
    }

    fun addParentCode(code: String) {
        parentsConnectivityCodes.add(code)
        callBack?.codesUpdated(parentsConnectivityCodes)
    }

    fun setChildPhoneNumber(childPhoneNumber: String?) {
        this.childPhoneNumber = childPhoneNumber
    }

    fun registerChild(
        successCallback: IChildRegistrationSuccess?,
        failureCallback: IChildRegistrationFailure?
    ) {
        checkInputValidation().let { errorType ->
            if (errorType != ErrorType.NOUN) {
                failureCallback?.onFailure(ChildProtectorException(errorType, "", null))
                return
            }
        }

        repository.registerChild(
            parentsConnectivityCodes,
            childPhoneNumber!!,
            object : ChildProtectorRepository.IChildRegistrationFinished {
                override fun onRegistrationSuccess(
                    child: User,
                    parents: ArrayList<User>
                ) {
                    successCallback?.onSuccesses(child, parents)
                }

                override fun onRegistrationFailure(error: ChildProtectorException) {
                    failureCallback?.onFailure(error)
                }
            })
    }

    private fun checkInputValidation(): @ErrorType Int {
        return when {
            childPhoneNumber == null ->
                ErrorType.MISSING_CHILD_PHONE_NUMBER

            childPhoneNumber!!.isEmpty() ->
                ErrorType.MISSING_CHILD_PHONE_NUMBER

            parentsConnectivityCodes.size < 1 ->
                ErrorType.MISSING_AT_LEAST_ONE_PARENT_CODE

            else ->
                ErrorType.NOUN
        }
    }

    interface IParentConnectivityCodesUpdated {
        fun codesUpdated(codes: ArrayList<String>)
    }

    interface IChildRegistrationSuccess {
        fun onSuccesses(child: User, parents: ArrayList<User>)
    }

    interface IChildRegistrationFailure {
        fun onFailure(error: ChildProtectorException)
    }
}