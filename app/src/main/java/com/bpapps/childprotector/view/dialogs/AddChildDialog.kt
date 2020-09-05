package com.bpapps.childprotector.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.ChildToConnectInfo

class AddChildDialog(private val callBack: IChildInfoAdded? = null) : DialogFragment() {

    private lateinit var etChildName: AppCompatEditText
    private lateinit var etChildPhoneNumber: AppCompatEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val inflater = requireActivity().layoutInflater
            val v: View = inflater.inflate(R.layout.dialog_child_info, null)

            etChildName = v.findViewById(R.id.etChildName)
            etChildPhoneNumber = v.findViewById(R.id.etChildPhoneNumber)

            val resources = resources
            val builder = AlertDialog.Builder(requireContext())

            return builder.setView(v)
                .setCancelable(false)
                .setTitle(resources.getString(R.string.add_child_dialog_title))
                .setPositiveButton(resources.getString(R.string.child_code_dialog_positive_button_text)) { _, _ ->
                    val name = etChildName.text.toString()
                    val phoneNumber = etChildPhoneNumber.text.toString()

                    if (checkInputValidation(name, phoneNumber)) {
                        callBack?.childAdded(ChildToConnectInfo(name, phoneNumber))
                    } else {
                        ErrorDialog(resources.getString(R.string.child_input_error)).show(
                            parentFragmentManager,
                            null
                        )
                    }

                }
                .setNegativeButton(resources.getString(R.string.parent_code_dialog_negative_button_text)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun checkInputValidation(name: String?, phoneNumber: String?): Boolean {
        return !(name == null || name == "" || phoneNumber == null || phoneNumber == "")
    }

    interface IChildInfoAdded {
        fun childAdded(childInfo: ChildToConnectInfo)
    }
}