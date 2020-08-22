package com.bpapps.childprotector.view.dialogs

class ClearInputDataDialog(
    private val title: String,
    private val message: String,
    private val iconId: Int? = null,
    private val clickCallBack: IOnCLickCallBack
) : ExitAppDialog(title, message, iconId, clickCallBack)