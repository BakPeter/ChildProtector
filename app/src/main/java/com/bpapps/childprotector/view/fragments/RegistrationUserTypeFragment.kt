package com.bpapps.childprotector.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.UserType
import com.bpapps.childprotector.view.dialogs.UserTypeChooserDialog

private const val TAG = "TAG.RegistrationUserTypeFragment"

class RegistrationUserTypeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_user_type, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.also { ab ->
            ab.show()
            ab.setDisplayHomeAsUpEnabled(false)
        }

        UserTypeChooserDialog(
            R.string.user_type_chooser_dialog_title,
            R.string.ok,
            object : UserTypeChooserDialog.IOnUserTypeChosen {
                @SuppressLint("LongLogTag")
                override fun onUserChosen(userType: UserType) {
//                    Log.d(TAG, userType.toString())
                    when (userType.type) {
                        UserType.CHILD ->
                            findNavController().navigate(R.id.action_registrationUserTypeFragment_to_childRegistrationFragment)
                        else ->//UserType.PARENT ->
                            findNavController().navigate(R.id.action_registrationUserTypeFragment_to_parentRegistrationFragment)
                    }
                }
            }).show(parentFragmentManager, null)
    }
}