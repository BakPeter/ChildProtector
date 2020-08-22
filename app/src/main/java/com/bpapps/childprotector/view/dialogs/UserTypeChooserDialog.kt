package com.bpapps.childprotector.view.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.UserType

private const val TAG = "TAG.UserTypeChooserDialog"

class UserTypeChooserDialog(
    private val _titleResId: Int,
    private val _okButtonTextResId: Int,
    private val _callBack: IOnUserTypeChosen? = null
) :
    DialogFragment() {

    @SuppressLint("LongLogTag")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var selectedItem: Int = 0
        val builder = AlertDialog.Builder(requireContext())

        return builder.setTitle(_titleResId)
            .setCancelable(false)
            .setSingleChoiceItems(
                R.array.user_types,
                0
            ) { _, which ->
                selectedItem = which
                Log.d(TAG, selectedItem.toString())
            }
            .setPositiveButton(_okButtonTextResId) { dialog, _ ->
                Log.d(TAG, selectedItem.toString())
                val userType = if (selectedItem == 0) UserType.PARENT else UserType.CHILD
                _callBack?.onUserChosen(UserType(userType))
                dialog.dismiss()
            }
            .create()
    }

    interface IOnUserTypeChosen {
        fun onUserChosen(userType: UserType)
    }
}