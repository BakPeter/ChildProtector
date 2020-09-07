package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.ChildToConnectInfo
import com.bpapps.childprotector.model.classes.ErrorType
import com.bpapps.childprotector.model.classes.User

class ParentRegistrationViewModel : ViewModel() {
    private val repository = ChildProtectorRepository.getInstance()

    private var childrenToConnectInfo: ArrayList<ChildToConnectInfo> = arrayListOf()

    var parentName: String? = null
    var parentPhoneNumber: String? = null
    private var childrenDataSetUpdatedCallBack: IChildrenToConnectInfoUpdated? = null

    private var isParentRegistered = false
    private var areAllChildrenRegistered = false
    private lateinit var registeredParent: User
    private val registeredChildren: ArrayList<User> = arrayListOf()
    val parentId: String?
        get() = registeredParent?._id

    fun getChildrenToConnectInfo() = childrenToConnectInfo

    fun addChildInfo(childInfo: ChildToConnectInfo) {
        childrenToConnectInfo.add(childInfo)
        childrenDataSetUpdatedCallBack?.dataUpdated(childrenToConnectInfo)
    }

    fun clearAllChildrenData() {
        childrenToConnectInfo.clear()
        parentPhoneNumber = null
        childrenDataSetUpdatedCallBack?.dataUpdated(childrenToConnectInfo)
    }

    fun registerForChildrenToConnectDataSetChanges(callBack: IChildrenToConnectInfoUpdated) {
        childrenDataSetUpdatedCallBack = callBack
    }

    fun unRegisterForChildrenToConnectDataSetChanges() {
        childrenDataSetUpdatedCallBack = null
    }

    fun registerParent(
        failureCallBack: IOnRegistrationFailure,
        successCallBack: IOnRegistrationSuccess
    ) {
        validateInput().let { result ->
            if (result != ErrorType.NOUN) {
                failureCallBack?.onFailure(result)
                return
            }
        }

        repository.registerParent(
            parentName!!,
            parentPhoneNumber!!,
            childrenToConnectInfo,
            object : ChildProtectorRepository.IParentRegistered {
                override fun onParentRegistered(parent: User?) {
                    parent?.let {
                        registeredParent = parent
                        isParentRegistered = true

                        if (isParentRegistered && areAllChildrenRegistered) {
                            successCallBack.onSuccess(registeredParent, registeredChildren)
                        }
                    }
                }
            },
            object : ChildProtectorRepository.IChildRegistered {
                override fun onChildRegistered(child: User?, childNumber: Int?) {
                    child?.let {
                        childNumber?.let {
                            registeredChildren.add(child)

                            if (childNumber == childrenToConnectInfo.size)
                                areAllChildrenRegistered = true

                            if (isParentRegistered && areAllChildrenRegistered) {
                                successCallBack.onSuccess(registeredParent, registeredChildren)
                            }
                        }
                    }
                }
            }
        )
    }

    private fun validateInput(): @ErrorType Int {
        if (parentName == null || parentName == "")
            return ErrorType.MISSING_PARENT_NAME

        if (parentPhoneNumber == null || parentPhoneNumber == "")
            return ErrorType.MISSING_PARENT_PHONE_NUMBER

        if (childrenToConnectInfo.size == 0)
            return ErrorType.ENTER_AT_LEAST_ONE_CHILD

        return ErrorType.NOUN
    }

    interface IChildrenToConnectInfoUpdated {
        fun dataUpdated(newDataSet: ArrayList<ChildToConnectInfo>)
    }

    interface IOnRegistrationSuccess {
        fun onSuccess(parent: User, children: ArrayList<User>)
    }

    interface IOnRegistrationFailure {
        fun onFailure(errorType: @ErrorType Int)
    }
}