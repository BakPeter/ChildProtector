package com.bpapps.childprotector.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bpapps.childprotector.R

open class ExitAppDialog(
    private val title: String,
    private val message: String,
    private val iconId: Int? = null,
    private val clickCallBack: IOnCLickCallBack
) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)

        message.let {
            builder.setMessage(message)
        }
        title.let {
            builder.setTitle(title)
        }
        iconId?.let {
            builder.setIcon(iconId)
        }

        builder.setPositiveButton(resources.getString(R.string.yes),
            DialogInterface.OnClickListener { _, _ ->
                clickCallBack.onClick(true)
            })
            .setNegativeButton(resources.getString(R.string.no),
                DialogInterface.OnClickListener { dialog, _ ->
                    clickCallBack.onClick(false)
                    dialog.dismiss()
                })
            .setCancelable(false)

        return builder.create()
    }

    interface IOnCLickCallBack {
        abstract fun onClick(retVal: Boolean)
    }
}
