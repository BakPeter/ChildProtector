package com.bpapps.childprotector.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bpapps.childprotector.R

class ErrorDialog(private val message: String?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val resources = resources
        val builder = AlertDialog.Builder(context)

        message?.let {
            builder.setMessage(it)
        }

        return builder.setTitle(resources.getString(R.string.error))
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}