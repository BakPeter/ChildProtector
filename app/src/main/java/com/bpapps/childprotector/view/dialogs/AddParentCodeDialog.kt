package com.bpapps.childprotector.view.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import com.bpapps.childprotector.R

private const val TAG = "TAG.AddParentCodeDialog"

class AddParentCodeDialog(private val _callBack: IOnParentCodeEntered? = null) : DialogFragment() {

    private lateinit var _etEnteredCode: AppCompatEditText

    @SuppressLint("LongLogTag")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_parent_code, null)
            _etEnteredCode = view.findViewById(R.id.etParentCode)

            val resources = resources
            val builder = AlertDialog.Builder(requireContext())

            builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.parent_code_dialog_positive_button_text)) { _, _ ->
                    Log.d(TAG, "CODE = ${_etEnteredCode.text}")
                    _callBack?.onCodeEntered(_etEnteredCode.text.toString())
                }
                .setNegativeButton(resources.getString(R.string.parent_code_dialog_negative_button_text)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface IOnParentCodeEntered {
        fun onCodeEntered(code: String)
    }
}