package com.bpapps.childprotector.view.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.bpapps.childprotector.R

class WaiteDialog(private val title: String) : DialogFragment() {

    @SuppressLint("LongLogTag")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val inflater = requireActivity().layoutInflater
            val v: View = inflater.inflate(R.layout.dialog_waite, null)

            val builder = android.app.AlertDialog.Builder(requireContext())

            return builder.setView(v)
                .setCancelable(false)
                .setTitle(title)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}